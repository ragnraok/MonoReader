from sqlalchemy.sql.expression import desc
from flask import current_app

from apibase import BaseArticleListView
from objects import fill_list_article_object
from utils import SITE_NOT_EXIST, PAGE_SMALL_THAN_ONE
from mono.models import Article

class SiteArticleListView(BaseArticleListView):
    """
    response format:
    {
        error_code: error_code,
        articles: [
            timeline article object
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
