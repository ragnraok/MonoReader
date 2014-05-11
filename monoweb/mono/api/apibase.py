from flask.views import View
from utils import make_api_response

class BaseArticleListView(View):
    methods = ['GET', 'POST']

    def __init__(self, page, *args, **kwargs):
        self.page = page

    def get_article_list(self):
        raise NotImplementedError()

    def dispatch_request(self, *args, **kwargs):
        """
        response format:
        {
            error_code: error_code,
            articles: [
                xxx_article_object,
                ...
            ]
        }
        """
        return make_api_response(error_code=0, data={'articles': self.get_article_list()})
