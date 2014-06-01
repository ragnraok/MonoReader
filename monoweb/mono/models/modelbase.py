from flask.ext.sqlalchemy import BaseQuery
from mono.database import db
from mono.feed import get_feed_url

class ModelMixin(object):
    def save(self):
        db.session.add(self)
        db.session.commit()
        return self

    def delete(self):
        db.session.delete(self)
        db.session.commit()
        return self

    def delete_without_commit(self):
        db.session.delete(self)

    def save_without_commit(self):
        db.session.add(self)

class MonoQuery(BaseQuery):
    def is_exist(self, id):
        if self.get(id) is not None:
            return True
        else:
            return False

class FavArticleQuery(MonoQuery):
    def is_fav(self, title):
        if self.filter_by(title=title).count() > 0:
            return True
        return False

class SiteQuery(MonoQuery):
    def daily_read_sites(self):
        sites = self.filter_by(is_read_daily=True).all()
        return sites

    def is_exist_by_url(self, url):
        if self.filter_by(url=url).count() > 0:
            return True
        return False

    def get_by_url(self, url):
        s = self.filter_by(url=url).first()
        if not s:
            s = self.filter_by(origin_url=url).first()
        return s

class CategorQuery(MonoQuery):
    def is_exist_by_name(self, name):
        if self.filter_by(name=name).count() > 0:
            return True
        return False
