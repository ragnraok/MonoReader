from flask import Blueprint

api_app = Blueprint("api", __name__)

def config_api_url():
    from timeline import MainTimelineView, DailyReadTimelineView, TimelineCheckView
    api_app.add_url_rule("/timeline/<int:page>/", view_func=MainTimelineView.as_view("timeline"))
    api_app.add_url_rule("/fav_site_timeline/<int:page>/", view_func=DailyReadTimelineView.as_view("daily_read_timeline"))
    main_timeline_check_view = TimelineCheckView.as_view("timeline_check", is_read_daily=False)
    fav_timeline_check_view = TimelineCheckView.as_view("fav_timeline_check", is_read_daily=True)
    api_app.add_url_rule("/timeline/check_update/", view_func=main_timeline_check_view)
    api_app.add_url_rule("/fav_site_timeline/check_update", view_func=fav_timeline_check_view)

    from site import SiteArticleListView, SitesListView, FavSiteSetView
    site_article_listview = SiteArticleListView.as_view('site_article_list')
    site_listview = SitesListView.as_view('site_listview')
    site_listview_by_category = SitesListView.as_view('site_listview_by_category', is_arrange_by_category=True)
    api_app.add_url_rule("/site/<int:site_id>/articles/", view_func=site_article_listview) # get all articles
    api_app.add_url_rule("/site/<int:site_id>/articles/<int:page>/", view_func=site_article_listview)
    api_app.add_url_rule("/site/get_all/", view_func=site_listview)
    api_app.add_url_rule("/site/get_all_by_category/", view_func=site_listview_by_category)
    api_app.add_url_rule("/site/get_by_category/<string:category>/", view_func=site_listview_by_category)
    api_app.add_url_rule("/site/get_by_category_id/<int:category_id>/", view_func=site_listview_by_category)
    api_app.add_url_rule("/site/fav_set/", view_func=FavSiteSetView.as_view("fav_site_setview"))

    from category import CategoryListView, CategorySetView
    category_setview = CategorySetView.as_view('category_setview', is_set=True)
    category_unsetview = CategorySetView.as_view('category_unsetview', is_set=False)
    api_app.add_url_rule("/category/get_all/", view_func=CategoryListView.as_view('category_listview'))
    api_app.add_url_rule("/category/set/", view_func=category_setview)
    api_app.add_url_rule("/category/unset/", view_func=category_unsetview)

    from subscribe import SiteSubscribeView
    subscribe_view = SiteSubscribeView.as_view('subscribe_view', is_subscribe=True)
    unsubscribe_view = SiteSubscribeView.as_view('unsubscribe_view', is_subscribe=False)
    api_app.add_url_rule("/subscribe/", view_func=subscribe_view)
    api_app.add_url_rule("/unsubscribe/", view_func=unsubscribe_view)

    from article import ArticleLoadView, ArticleFavSetView, FavArticleListView, FavArticleListCheckView
    load_article_view = ArticleLoadView.as_view('load_article_view', is_load_fav=False)
    load_fav_article_view = ArticleLoadView.as_view('load_fav_article_view', is_load_fav=True)
    fav_article_setview = ArticleFavSetView.as_view('fav_article_setview', is_fav=True)
    unfav_article_setview = ArticleFavSetView.as_view('unfav_article_setview', is_fav=False)
    fav_article_listview = FavArticleListView.as_view('fav_article_listview')
    fav_article_list_update_check = FavArticleListCheckView.as_view('fav_article_list_update_check')
    api_app.add_url_rule("/article/load/<int:article_id>/", view_func=load_article_view)
    api_app.add_url_rule("/article/load_fav/<int:article_id>/", view_func=load_fav_article_view)
    api_app.add_url_rule("/article/fav/", view_func=fav_article_setview)
    api_app.add_url_rule("/article/unfav/", view_func=unfav_article_setview)
    api_app.add_url_rule("/article/fav_list/", view_func=fav_article_listview)
    api_app.add_url_rule("/article/fav_list/<int:page>/", view_func=fav_article_listview)
    api_app.add_url_rule("/article/fav_list/check_update/", view_func=fav_article_list_update_check)
