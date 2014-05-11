from flask import Blueprint
from flask.views import View

api_app = Blueprint("api", __name__)

class TestView(View):
    def dispatch_request(self):
        return "123123"

def config_api_url():
    from timeline import MainTimelineView, DailyReadTimelineView
    api_app.add_url_rule("/timeline/<int:page>/", view_func=MainTimelineView.as_view("timeline"))
    api_app.add_url_rule("/fav_site_timeline/<int:page>/", view_func=DailyReadTimelineView.as_view("daily_read_timeline"))
    #api_app.add_url_rule("/test/<int:page>/", view_func=TestView.as_view("test"))
