from flask import Blueprint
from flask.views import View

api_app = Blueprint("api", __name__)

def config_api_url():
    from timeline import MainTimelineView, DailyReadTimelineView
    api_app.add_url_rule("/timeline/<int:page>/", view_func=MainTimelineView.as_view("timeline"))
    api_app.add_url_rule("/fav_site_timeline/<int:page>/", view_func=DailyReadTimelineView.as_view("daily_read_timeline"))

    from site import SiteArticleListView
    site_article_listview = SiteArticleListView.as_view('site_article_list')
    api_app.add_url_rule("/site/<int:site_id>/articles/", view_func=site_article_listview) # get all articles
    api_app.add_url_rule("/site/<int:site_id>/articles/<int:page>/", view_func=site_article_listview)
