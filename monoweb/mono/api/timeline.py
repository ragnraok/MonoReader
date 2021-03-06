from flask import current_app
from sqlalchemy.sql.expression import desc, select

from utils import PAGE_SMALL_THAN_ONE
from apibase import BaseArticleListView, BaseAPIPOSTView, BaseDataChangeCheckView
from mono.models import Article, Site
from objects import fill_list_article_object, fill_change_date_object
from mono import cache

import datetime
import calendar

class MainTimelineView(BaseArticleListView):
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
        page = kwargs.get('page', 1)
        if page >= 1:
            #article_list = Article.query.filter(Article.site_id!=None).order_by(
            #        desc(Article.updated)).paginate(page=page,
            #                per_page=per_page_num, error_out=False).items
            article_list = Article.query.filter(Article.site_id.in_(
                Site.query.with_entities(Site.id).filter(Site.is_subscribe==True))).filter(
                        Article.id!=None).order_by(desc(Article.updated)).paginate(
                                page=page, per_page=per_page_num, error_out=False).items
        else:
            raise ValueError(PAGE_SMALL_THAN_ONE)
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title,
                article.site.title, article.updated, article.first_image_url,
                article.is_fav))
        return result

class DailyReadTimelineView(BaseArticleListView):
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
        page = kwargs.get('page', 1)
        if page >= 1:
            article_list = Article.query.filter(Article.site_id.in_(
                Site.query.with_entities(Site.id).filter(Site.is_read_daily==True))).filter(
                        Article.id!=None).order_by(desc(Article.updated)).paginate(
                                page=page, per_page=per_page_num, error_out=False).items
        else:
            raise ValueError(PAGE_SMALL_THAN_ONE)
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title, article.site.title,
                article.updated, article.first_image_url, article.is_fav))
        return result

class TimelineCheckView(BaseDataChangeCheckView):
    def __init__(self, is_daily_read_timeline=False, **kwargs):
        super(TimelineCheckView, self).__init__()
        self.is_daily_read_timeline = is_daily_read_timeline

    def get_data(self):
        if self.is_daily_read_timeline:
            key = current_app.config['FAV_TIMELINE_UPDATE_CACHE_KEY']
        else:
            key = current_app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']
        update = cache[key]
        if update:
            return fill_change_date_object(update)
        else:
            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            return fill_change_date_object(now_timestamp)

