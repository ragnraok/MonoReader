from flask import current_app

from utils import SITE_NOT_EXIST, ARTICLE_NOT_EXIST, PAGE_SMALL_THAN_ONE
from objects import fill_article_object
from mono.models import Site, Article, FavArticle
from apibase import BaseAPIGETView, BaseAPIPOSTView, BaseArticleListView

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
        else:
            article = FavArticle.query.get(article_id)

        if article is None:
            return {}
        else:
            return fill_article_object(article_id=article.id, title=article.title,
                    site=article.site.title, updated=article.updated, content=article.content,
                    url=article.url)

class ArticleFavSetView(BaseAPIPOSTView):
    def __init__(self, is_fav=True, **kwargs):
        self.is_fav = True
        super(ArticleFavSetView, self).__init__()

    def proc_data(self, data, **kwargs):
        if self.is_fav:
            """
            post data format:
                {
                    article_id: article_id
                }
            """
            article_id = data.get('article_id')
            article = Article.query.get(article_id)
            if article is None:
                raise ValueError(ARTICLE_NOT_EXIST)
            article.fav()
        else:
            """
            post data format:
                {
                    fav_article_id: fav_article_id
                }
            """
            fav_article_id = data.get('fav_article_id')
            fav_article = FavArticle.query.get(fav_article_id)
            if fav_article is None:
                raise ValueError(ARTICLE_NOT_EXIST)
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
        page = kwargs.get('page', 1)
        if page >= 1:
            article_list = FavArticle.query.order_by(desc(Article.updated)).paginate(
                    page=page, per_page=per_page_num, error_out=False).items
        else:
            raise ValueError(PAGE_SMALL_THAN_ONE)
        result = []
        for article in article_list:
            result.append(fill_list_article_object(article.id, article.title,
                article.site_title, article.updated, article.first_image_url))
        return result
