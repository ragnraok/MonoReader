from flask import current_app

from mono import cache
from mono.database import db
from mono.feed import FeedDataFetcher
from modelbase import ModelMixin, MonoQuery, FavArticleQuery, SiteQuery

import datetime
import calendar

class Site(db.Model, ModelMixin):
    __tablename__ = 'site'
    query_class = SiteQuery

    id = db.Column(db.Integer, primary_key=True)
    origin_url = db.Column(db.String(120), nullable=True)
    url = db.Column(db.String(120), nullable=False)
    title = db.Column(db.Text)
    updated = db.Column(db.DateTime, default=datetime.datetime.utcnow)
    is_read_daily = db.Column(db.Boolean, default=False)
    #articles = db.relationship('Article', backref='site', lazy='dynamic', cascade='all,delete')
    articles = db.relationship('Article', backref='site', lazy='dynamic')
    category_id = db.Column(db.Integer, db.ForeignKey('category.id'))
    is_subscribe = db.Column(db.Boolean, default=True)

    #def __init__(self, *args, **kwargs):
    #    super(Site, self).__init__(*args, **kwargs)

    def __repr__(self):
        return "<Site: updated at %s, url: %s>" % (self.updated.strftime("%Y-%m-%d"), self.url)

    def update_site(self):
        data_fetcher = FeedDataFetcher(self.url, False)
        updated = data_fetcher.fetch_site_updated_time()
        is_new_article = False
        if updated > self.updated or self.articles.count() == 0:
            #self.delete_all_articles()
            if self.title is None:
                self.title = data_fetcher.fetch_site_title()
            self.__update_articles_from_fetcher(data_fetcher)
            if self.origin_url is None:
                self.origin_url = self.url
                self.url = data_fetcher.url
            self.updated = updated
            is_new_article = True

        self.save()
        return is_new_article

    def __update_articles_from_fetcher(self, data_fetcher):
        articles_list = data_fetcher.fetch_articles()
        if articles_list is not None:
            for item in articles_list:
                if not Article.query.is_exist_by_url(item['url']):
                    article = Article(title=item['title'], content=item['content'],
                            url=item['url'], updated=item['date'], site_id=self.id,
                            first_image_url=item['first_img_url'], site_title=self.title)
                    article.save_without_commit()

    def delete_all_articles(self):
        for a in self.articles:
            a.delete_without_commit()
        self.save()

    def unset_category(self):
        unclassified_name = current_app.config.get('UNCLASSIFIED', "not classified")
        unclassified = Category.query.filter_by(name=unclassified_name).first()
        if unclassified is not None:
            self.category_id = unclassified.id
            self.save()
        else:
            unclassified = Category(name=unclassified_name)
            unclassified.save()
            self.category_id = unclassified.id
            self.save()


    def set_category(self, category):
        self.category_id = category.id
        self.save()

    def set_category_by_id(self, category_id):
        if Category.query.is_exist(category_id):
            self.category_id = category_id
            self.save()

    def set_category_by_name(self, category_name):
        category = Category.query.filter_by(name=category_name)
        if category is None:
            category = Category(name=category_name)
            category.save()
        self.set_category(category)


class ArticleQuery(MonoQuery):
    def is_exist_by_url(self, url):
        if self.with_entities(Article.url).filter_by(url=url).count() > 0:
            return True
        return False

class Article(db.Model, ModelMixin):
    __tablename__ = 'article'
    query_class = ArticleQuery

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.Text, nullable=False)
    content = db.Column(db.Text, nullable=False)
    updated = db.Column(db.DateTime, default=datetime.datetime.now)
    url = db.Column(db.String(120), nullable=False)
    site_id = db.Column(db.Integer, db.ForeignKey('site.id'))
    site_title = db.Column(db.Text, nullable=False)
    first_image_url = db.Column(db.Text, nullable=True)
    is_fav = db.Column(db.Boolean, default=False)

    def __repr__(self):
        return "<Article: %s, updated at %s>" % (self.title.encode('utf-8'), self.updated)

    def fav(self):
        if not self.is_fav:
            #fav_article = FavArticle(title=self.title, content=self.content,
            #        updated=self.updated, url=self.url, site_title=self.site.title,
            #        first_image_url=self.first_image_url)
            #fav_article.save()

            self.is_fav = True
            self.save()

            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            cache[current_app.config['FAV_ARTICLE_LIST_UPDATE_CACHE_KEY']] = now_timestamp
            cache[current_app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp
            if self.site is not None and self.site.is_read_daily:
                cache[current_app.config['FAV_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp

    def unfav(self):
        if self.is_fav:
            self.is_fav = False
            self.save()

            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            cache[current_app.config['FAV_ARTICLE_LIST_UPDATE_CACHE_KEY']] = now_timestamp
            cache[current_app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp
            if self.site is not None and self.site.is_read_daily:
                cache[current_app.config['FAV_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp

    #def is_fav(self):
    #    if hasattr(self, "_is_fav"):
    #        return self._is_fav
    #    if FavArticle.query.is_fav(self.title):
    #        self._is_fav = True
    #    else:
    #        self._is_fav = False
    #    return self._is_fav


#category_site = db.Table('category_site',
#        db.Column('category_id', db.Integer, db.ForeignKey('category.id')),
#        db.Column('site_id', db.Integer, db.ForeignKey('site.id'))
#        )

class Category(db.Model, ModelMixin):
    __tablename__ = 'category'
    query_class = MonoQuery

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False, unique=True)
    #sites = db.relationship('Site', secondary=category_site, backref=db.backref('categories',
    #    lazy='dynamic'), lazy='dynamic')
    sites = db.relationship('Site', backref='category', lazy='dynamic')

    def __repr__(self):
        return "<Category: %s>" % self.name

#class FavArticle(db.Model, ModelMixin):
#    __tablename__ = 'favarticle'
#    query_class = FavArticleQuery
#
#    id = db.Column(db.Integer, primary_key=True)
#    title = db.Column(db.Text, nullable=False)
#    content = db.Column(db.Text, nullable=False)
#    updated = db.Column(db.DateTime, default=datetime.datetime.now)
#    url = db.Column(db.String(120), nullable=False)
#    #site_id = db.Column(db.Integer, db.ForeignKey('site.id'))
#    site_title = db.Column(db.Text, nullable=False)
#    fav_date = db.Column(db.DateTime, default=datetime.datetime.now)
#    first_image_url = db.Column(db.Text, nullable=True)
#
#    def __repr__(self):
#        return "<FavArticle: %s, updated at %s>" % (self.title.encode('utf-8'), self.updated)
#
#    def delete(self):
#        article = Article.query.filter_by(title=self.title).first()
#        if article:
#            article.is_fav = False
#            article.save()
#        now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
#        cache[current_app.config['FAV_ARTICLE_LIST_UPDATE_CACHE_KEY']] = now_timestamp
#        cache[current_app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp
#        site = Site.query.filter_by(title=self.site_title).first()
#        if site is not None and site.is_read_daily:
#            print site
#            cache[current_app.config['FAV_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp
#        super(FavArticle, self).delete()
#
#class TestModel(db.Model):
#    id = db.Column(db.String(120), primary_key=True)
#    test = db.Column(db.Text, nullable=True)
