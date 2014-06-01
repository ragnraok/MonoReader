from flask import current_app
from sqlalchemy.sql.expression import desc

from utils import SITE_NOT_EXIST, ARTICLE_NOT_EXIST, PAGE_SMALL_THAN_ONE, DATA_FORMAT_ERROR
from objects import fill_article_object, fill_list_article_object, fill_change_date_object
from mono.models import Site, Article
from apibase import BaseAPIGETView, BaseAPIPOSTView, BaseArticleListView, BaseDataChangeCheckView
from mono import cache

import datetime
import calendar

class ArticleLoadView(BaseAPIGETView):
    def __init__(self, **kwargs):
        super(ArticleLoadView, self).__init__(**kwargs)
        self.data_key = 'article'

    def get_data(self, **kwargs):
        """
        response format:
        {
            error_code: error_code,
            data: {
                article: article object
            }
        }
        """
        article_id = kwargs.get('article_id', 1)
        article = Article.query.get(article_id)
        is_fav = article.is_fav

        if article is None:
            raise ValueError(ARTICLE_NOT_EXIST)
        else:
            site_title = article.site_title
            return fill_article_object(article_id=article.id, title=article.title,
                    site=site_title, updated=article.updated, content=article.content,
                    url=article.url, cover_url=article.first_image_url, is_fav=is_fav)

class ArticleFavSetView(BaseAPIPOSTView):
    def __init__(self, is_fav=True, **kwargs):
        self.is_fav = is_fav
        super(ArticleFavSetView, self).__init__()

    def proc_data(self, data, **kwargs):
        if self.is_fav:
            """
            post data format:
                {
                    article_id: article_id,
                }
            """
            article_id = data.get('article_id', None)
            if article_id is None:
                raise ValueError(DATA_FORMAT_ERROR)
            article = Article.query.get(article_id)
            if article is None:
                raise ValueError(ARTICLE_NOT_EXIST)
            article.fav()
        else:
            """
            post data format:
                {
                    article_id: fav_article_id,
                }
            """
            article_id = data.get('article_id', None)
            if article_id is None:
                raise ValueError(DATA_FORMAT_ERROR)
            fav_article = Article.query.get(article_id)
            if fav_article is None:
                raise ValueError(ARTICLE_NOT_EXIST)
            if fav_article:
                fav_article.unfav()

class FavArticleListView(BaseArticleListView):
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
        per_page_num = current_app.config.get('ARTICLE_NUM_PER_PAGE', 10)
        if 'page' in kwargs:
            page = kwargs.get('page', 1)
            if page >= 1:
                article_list = Article.query.filter_by(is_fav=True).order_by(
                        desc(Article.updated)).paginate(page=page,
                                per_page=per_page_num, error_out=False).items
            else:
                raise ValueError(PAGE_SMALL_THAN_ONE)
        else:
            article_list = Article.query.filter_by(is_fav=True).order_by(desc(
                Article.updated)).all()
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title,
                article.site_title, article.updated, article.first_image_url,
                True))
        return result

class FavArticleListCheckView(BaseDataChangeCheckView):
    def get_data(self):
        key = current_app.config['FAV_ARTICLE_LIST_UPDATE_CACHE_KEY']
        update = cache[key]
        if update:
            print "check fav article list, update is not null"
            return fill_change_date_object(update)
        else:
            print "check fav article list, update is null"
            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            return fill_change_date_object(now_timestamp)
