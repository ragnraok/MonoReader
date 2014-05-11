from flask.views import View
from utils import make_api_response, SUCCESS

class BaseArticleListView(View):
    methods = ['GET', ]

    def get_article_list(self, **kwargs):
        raise NotImplementedError()

    def dispatch_request(self, **kwargs):
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
        try:
            articles = self.get_article_list(**kwargs)
            return make_api_response(error_code=SUCCESS, data={'articles': articles})
        except ValueError, e:
            return make_api_response(error_code=e.message, data=None)
