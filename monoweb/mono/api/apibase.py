from flask.views import View
from utils import make_api_response, SUCCESS

class BaseAPIView(View):
    def get_data(self, **kwargs):
        raise NotImplementedError()

    def dispatch_request(self, **kwargs):
        try:
            data = self.get_data(**kwargs)
            return make_api_response(error_code=SUCCESS, data={self.data_key: data})
        except ValueError, e:
            return make_api_response(error_code=e.message, data=None)

class BaseArticleListView(BaseAPIView):
    methods = ['GET', ]

    def __init__(self):
        super(BaseArticleListView, self).__init__()
        self.data_key = 'articles'

    def get_data(self, **kwargs):
        return self.get_article_list(**kwargs)

    def get_article_list(self, **kwargs):
        raise NotImplementedError()
