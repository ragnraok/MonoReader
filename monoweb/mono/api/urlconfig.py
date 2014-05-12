from flask import Blueprint
from flask.views import View

api_app = Blueprint("api", __name__)

def config_api_url():
    from timeline import MainTimelineView, DailyReadTimelineView
    api_app.add_url_rule("/timeline/<int:page>/", view_func=MainTimelineView.as_view("timeline"))
    api_app.add_url_rule("/fav_site_timeline/<int:page>/", view_func=DailyReadTimelineView.as_view("daily_read_timeline"))

    from site import SiteArticleListView, SitesListView
    site_article_listview = SiteArticleListView.as_view('site_article_list')
    api_app.add_url_rule("/site/<int:site_id>/articles/", view_func=site_article_listview) # get all articles
    api_app.add_url_rule("/site/<int:site_id>/articles/<int:page>/", view_func=site_article_listview)

    site_listview = SitesListView.as_view('site_listview')
    site_listview_by_category = SitesListView.as_view('site_listview_by_category', is_arrange_by_category=True)
    api_app.add_url_rule("/site/get_all/", view_func=site_listview)
    api_app.add_url_rule("/site/get_all_by_category/", view_func=site_listview_by_category)
    api_app.add_url_rule("/site/get_by_category/<string:category>/", view_func=site_listview_by_category)
    api_app.add_url_rule("/site/get_by_category_id/<int:category_id>/", view_func=site_listview_by_category)
