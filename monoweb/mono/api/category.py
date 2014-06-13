from flask import current_app
from sqlalchemy.sql.expression import desc, select

from apibase import BaseAPIGETView, BaseAPIPOSTView, BaseArticleListView
from objects import fill_category_object, fill_list_article_object
from utils import SITE_NOT_EXIST, DATA_FORMAT_ERROR, PAGE_SMALL_THAN_ONE
from mono.models import Site, Category, Article

class CategoryListView(BaseAPIGETView):
    """
    response format:
    {
        error_code: error_code,
        data: {
            category: [
                category object
                ...
            ]
        }
    }
    """
    def __init__(self, **kwargs):
        self.data_key = "category"

    def get_data(self, **kwargs):
        category_list = Category.query.all()
        result = []
        unclassified_name = current_app.config.get('UNCLASSIFIED', "not classified")
        for category in category_list:
            result.append(fill_category_object(category_id=category.id, name=category.name,
                is_un_classified=category.name==unclassified_name))
        return result

class CategorySetView(BaseAPIPOSTView):
    def __init__(self, is_set=True, **kwargs):
        super(CategorySetView, self).__init__(**kwargs)
        self.is_set = is_set

    def proc_data(self, data, **kwargs):
        """
        post data format:
            1. set category:
            {
                site_id: site_id,
                category: category_name
            }
            2. unset category
            {
                site_id: site_id
            }
        """
        site_id = data.get('site_id', None)
        category_name = data.get('category', None)
        if site_id is None:
            raise ValueError(DATA_FORMAT_ERROR)
        if self.is_set is True and category_name is None:
            raise ValueError(DATA_FORMAT_ERROR)
        site = Site.query.get(site_id)
        if site is None:
            raise ValueError(SITE_NOT_EXIST)
        if self.is_set:
            category = Category.query.filter_by(name=category_name).first()
            if category is None:
                category = Category(name=category_name)
                category.save()
            site.set_category(category)
        else:
            site.unset_category()

class CategoryTimeline(BaseArticleListView):
    def __init__(self, is_un_classified=False, **kwargs):
        super(CategoryTimeline, self).__init__(**kwargs)
        self.is_un_classified = is_un_classified

    """
    response format:
    {
        error_code: error_code,
        data: {
            articles: [
                list article object
                ...
            ]
        }
    }
    """
    def get_article_list(self, **kwargs):
        if self.is_un_classified:
            unclassified_name = current_app.config.get('UNCLASSIFIED', "not classified")
            category = Category.query.filter_by(name=unclassified_name).first()
        else:
            category_name = kwargs.get('category', None)
            if category_name is None:
                raise ValueError(DATA_FORMAT_ERROR)
            category = Category.query.filter_by(name=category_name).first()
        page = kwargs.get('page', 1)
        if page < 1:
            raise ValueError(PAGE_SMALL_THAN_ONE)
        per_page_num = current_app.config.get('ARTICLE_NUM_PER_PAGE', 10)
        if category is not None:
            ss = Site.query.filter_by(is_subscribe=True).all()
            ssids = [item.id for item in ss]
            ids = [item.id for item in category.sites]
            ids = set(ssids).intersection(set(ids))
            ids = list(ids)
            article_list = Article.query.filter(Article.site_id.in_(ids)).order_by(
                    desc(Article.updated)).paginate(page=page, per_page=per_page_num, error_out=False).items
        else:
            article_list = []
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title,
                article.site.title, article.updated, article.first_image_url,
                article.is_fav))
        return result
