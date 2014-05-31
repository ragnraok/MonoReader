from flask import current_app
from sqlalchemy.sql.expression import desc

from utils import SITE_NOT_EXIST, ARTICLE_NOT_EXIST, PAGE_SMALL_THAN_ONE
from objects import fill_article_object, fill_list_article_object, fill_change_date_object
from mono.models import Site, Article, FavArticle
from apibase import BaseAPIGETView, BaseAPIPOSTView, BaseArticleListView, BaseDataChangeCheckView
from mono import cache

import datetime
import calendar

class ArticleLoadView(BaseAPIGETView):
    def __init__(self, is_load_fav=False, **kwargs):
        super(ArticleLoadView, self).__init__(**kwargs)
        self.data_key = 'article'
        self.is_load_fav = is_load_fav

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
        if not self.is_load_fav:
            article = Article.query.get(article_id)
            is_fav = article.is_fav
        else:
            article = FavArticle.query.get(article_id)
            is_fav = True

        if article is None:
            raise ValueError(ARTICLE_NOT_EXIST)
        else:
            if self.is_load_fav:
                site_title = article.site_title
            else:
                site_title = article.site.title
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
                    is_get_from_fav_article: boolean(optional, default is False)
                }
            """
            article_id = data.get('article_id', None)
            is_get_from_fav_article = data.get('is_get_from_fav_article', False)
            if article_id is None:
                raise ValueError(DATA_FORMAT_ERROR)
            article = Article.query.get(article_id)
            if article is None:
                if is_get_from_fav_article:
                    fav_article = FavArticle.query.get(article_id)
                    if fav_article:
                        article = Article.query.filter_by(title=fav_article.title).first()
                else:
                    raise ValueError(ARTICLE_NOT_EXIST)
            article.fav()
        else:
            """
            post data format:
                {
                    fav_article_id: fav_article_id,
                    is_get_from_article: boolean(optional, default is False)
                }
            """
            fav_article_id = data.get('fav_article_id', None)
            is_get_from_article = data.get('is_get_from_article', False)
            if fav_article_id is None:
                raise ValueError(DATA_FORMAT_ERROR)
            fav_article = FavArticle.query.get(fav_article_id)
            if fav_article is None:
                if is_get_from_article:
                    article = Article.query.get(fav_article_id)
                    if article:
                        fav_article = FavArticle.query.filter_by(title=article.title).first()
                else:
                    raise ValueError(ARTICLE_NOT_EXIST)
            print fav_article
            if fav_article:
                fav_article.delete()

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
                article_list = FavArticle.query.order_by(desc(FavArticle.updated)).paginate(
                        page=page, per_page=per_page_num, error_out=False).items
            else:
                raise ValueError(PAGE_SMALL_THAN_ONE)
        else:
            article_list = FavArticle.query.order_by(desc(
                FavArticle.updated)).all()
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
