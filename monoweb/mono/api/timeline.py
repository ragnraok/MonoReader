from flask import current_app
from sqlalchemy.sql.expression import desc, select

from utils import PAGE_SMALL_THAN_ONE
from apibase import BaseArticleListView
from mono.models import Article, Site
from objects import fill_list_article_object

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
            article_list = Article.query.order_by(desc(Article.updated)).paginate(
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
                Site.query.with_entities(Site.id).filter(Site.is_read_daily==True))).order_by(desc(
                Article.updated)).paginate(page=page, per_page=per_page_num, error_out=False).items
        else:
            raise ValueError(PAGE_SMALL_THAN_ONE)
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title, article.site.title,
                article.updated, article.first_image_url, article.is_fav))
        return result
