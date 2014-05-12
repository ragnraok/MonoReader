from sqlalchemy.sql.expression import desc
from flask import current_app

from apibase import BaseArticleListView, BaseSiteListView
from objects import fill_list_article_object, fill_site_object
from utils import SITE_NOT_EXIST, PAGE_SMALL_THAN_ONE
from mono.models import Article, Site, Category

class SiteArticleListView(BaseArticleListView):
    """
    response format:
    {
        error_code: error_code,
        articles: [
            list article object
            ...
        ]
    }
    """
    def get_article_list(self, **kwargs):
        article_list = []
        site_id = kwargs.get('site_id', 1)
        if 'page' in kwargs:
            per_page_num = current_app.config.get('ARTICLE_NUM_PER_PAGE', 10)
            page = kwargs.get('page', 1)
            if page < 1:
                raise ValueError(PAGE_SMALL_THAN_ONE)
            article_list = Article.query.filter_by(site_id=site_id).order_by(desc(
                Article.updated)).paginate(page=page, per_page=per_page_num).items
        else:
            article_list = Article.query.filter_by(site_id=site_id).order_by(desc(
                Article.updated)).all()
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.title,
                article.site.title, article.updated))
        return result

class SiteList(object):

    def __get_site_object_list(self, site_list):
        result = []
        for site in site_list:
            result.append(fill_site_object(title=site.title, updated=site.updated,
                category=site.category, is_read_daily=site.is_read_daily,
                article_count=site.articles.count(), url=site.url))
        return result


    def get_all_sites(self):
        site_list = Site.query.all()
        return self.__get_site_object_list(site_list)

    def get_sites_by_cateogry(self, category):
        site_list = category.sites
        return self.__get_site_object_list(site_list)

    def get_sites_by_cateogry_id(self, category_id):
        category = Category.query.get(category_id)
        if category:
            site_list = category.sites
            return self.__get_site_object_list(site_list)

class SitesListView(BaseSiteListView):
    def __init__(self, is_arrange_by_category=False, **kwargs):
        super(SitesListView, self).__init__()
        self.is_arrange_by_category = is_arrange_by_category
        self.site_list = SiteList()

    def get_sites(self, **kwargs):
        if self.is_arrange_by_category is False:
            return self.site_list.get_all_sites()
        else:
            category = kwargs.get('category', None)
            category_id = kwargs.get('category_id', None)
            if category is not None:
                category = Category.query.filter_by(name=category).first()
                return self.site_list.get_sites_by_cateogry(category)
            elif category_id is not None:
                return self.site_list.get_sites_by_cateogry_id(category_id)
            else:
                category_list = Category.query.all()
                result = []
                for category in category_list:
                    result.append({'category': category.name,
                        'sites': self.site_list.get_sites_by_cateogry(category)})
                return result
