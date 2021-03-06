from sqlalchemy.sql.expression import desc
from flask import current_app

from apibase import BaseArticleListView, BaseSiteListView, BaseAPIPOSTView
from objects import fill_list_article_object, fill_site_object
from utils import SITE_NOT_EXIST, PAGE_SMALL_THAN_ONE, DATA_FORMAT_ERROR
from mono.models import Article, Site, Category

class SiteArticleListView(BaseArticleListView):
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
        article_list = []
        site_id = kwargs.get('site_id', 1)
        if 'page' in kwargs:
            per_page_num = current_app.config.get('ARTICLE_NUM_PER_PAGE', 10)
            page = kwargs.get('page', 1)
            if page < 1:
                raise ValueError(PAGE_SMALL_THAN_ONE)
            article_list = Article.query.filter_by(site_id=site_id).order_by(desc(
                Article.updated)).paginate(page=page, per_page=per_page_num,
                        error_out=False).items
        else:
            article_list = Article.query.filter_by(site_id=site_id).order_by(desc(
                Article.updated)).all()
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title,
                article.site.title, article.updated, article.first_image_url,
                article.is_fav))
        return result

class SiteList(object):

    def __get_site_object_list(self, site_list):
        unclassified_name = current_app.config.get('UNCLASSIFIED', "not classified")
        result = []
        for site in site_list:
            #is_un_classified = site.category is not None \
            #    and site.category.name == unclassified_name or True
            if site.category is not None:
                is_un_classified = site.category.name == unclassified_name
            else:
                is_un_classified = True
            result.append(fill_site_object(site_id=site.id, title=site.title, updated=site.updated,
                category=site.category, is_read_daily=site.is_read_daily,
                article_count=site.articles.count(), url=site.url,
                is_un_classified=is_un_classified))
        return result

    def get_all_sites(self):
        site_list = Site.query.filter_by(is_subscribe=True)
        return self.__get_site_object_list(site_list)

    def get_sites_by_cateogry(self, category):
        if category:
            return self.__get_site_object_list(category.sites)
        else:
            return []

    def get_sites_by_cateogry_id(self, category_id):
        category = Category.query.get(category_id)
        if category:
            site_list = category.sites
            return self.__get_site_object_list(site_list)
        else:
            return []

class SitesListView(BaseSiteListView):
    def __init__(self, is_arrange_by_category=False, **kwargs):
        super(SitesListView, self).__init__()
        self.is_arrange_by_category = is_arrange_by_category
        self.site_list = SiteList()

    def get_sites(self, **kwargs):
        if self.is_arrange_by_category is False:
            """
            response format:
            {
                error_code: error_code,
                data: {
                    sites: [
                        site_object
                        ...
                    ]
                }
            }
            """
            return self.site_list.get_all_sites()
        else:
            category = kwargs.get('category', None)
            category_id = kwargs.get('category_id', None)
            if category is not None:
                """
                response format:
                {
                    error_code: error_code,
                    data: {
                        sites: [
                            site_object
                            ...
                        ]
                    }
                }
                """
                category = Category.query.filter_by(name=category).first()
                return self.site_list.get_sites_by_cateogry(category)
            elif category_id is not None:
                """
                response format:
                {
                    error_code: error_code,
                    data: {
                        sites: [
                            site_object
                            ...
                        ]
                    }
                }
                """
                return self.site_list.get_sites_by_cateogry_id(category_id)
            else:
                """
                response format:
                {
                    error_code: error_code,
                    data: {
                        sites: [
                            {
                                category: category_name,
                                is_un_classified: boolean,
                                sites: [
                                    site object
                                ]
                            }
                            ...
                        ]
                    }
                }
                """
                category_list = Category.query.all()
                result = []
                unclassified_name = current_app.config.get('UNCLASSIFIED', "not classified")
                for category in category_list:
                    result.append({
                        'category': category.name,
                        'is_un_classified': category.name == unclassified_name,
                        'sites': self.site_list.get_sites_by_cateogry(category)})
                return result

class FavSiteSetView(BaseAPIPOSTView):
    def proc_data(self, data, **kwargs):
        """
        post data format:
            {
                site_id: site_id,
                is_fav: boolean
            }
        """
        site_id = data.get('site_id', None)
        is_fav = data.get('is_fav', None)
        if site_id is None or is_fav is None:
            raise ValueError(DATA_FORMAT_ERROR)
        if not isinstance(is_fav, bool):
            raise ValueError(DATA_FORMAT_ERROR)
        site = Site.query.get(site_id)
        if site is None:
            raise ValueError(SITE_NOT_EXIST)
        #site.is_read_daily = is_fav
        #site.save()
        if is_fav:
            site.fav()
        else:
            site.unfav()
