from flask.views import View
from flask import request
from utils import make_api_response, get_post_data, SUCCESS

class BaseAPIGETView(View):
    methods = ['GET', ]

    def get_data(self, **kwargs):
        raise NotImplementedError()

    def dispatch_request(self, **kwargs):
        try:
            data = self.get_data(**kwargs)
            return make_api_response(error_code=SUCCESS, data={self.data_key: data})
        except ValueError, e:
            return make_api_response(error_code=e.message, data=None)

class BaseAPIPOSTView(View):
    methods = ['POST', ]

    def proc_data(self, data, **kwargs):
        pass

    def dispatch_request(self, **kwargs):
        try:
            data = get_post_data()
            self.proc_data(data, **kwargs)
            return make_api_response(error_code=SUCCESS)
        except ValueError, e:
            return make_api_response(error_code=e.message)


class BaseArticleListView(BaseAPIGETView):

    def __init__(self):
        super(BaseArticleListView, self).__init__()
        self.data_key = 'articles'

    def get_data(self, **kwargs):
        return self.get_article_list(**kwargs)

    def get_article_list(self, **kwargs):
        raise NotImplementedError()

class BaseSiteListView(BaseAPIGETView):

    def __init__(self):
        super(BaseSiteListView, self).__init__()
        self.data_key = 'sites'

    def get_data(self, **kwargs):
        return self.get_sites(**kwargs)

    def get_sites(self, **kwargs):
        raise NotImplementedError()

