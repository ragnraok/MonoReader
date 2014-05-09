from mono.database import db

import datetime

class Site(db.Model):
    __tablename__ = 'site'

    id = db.Column(db.Integer, primary_key=True)
    url = db.Column(db.String(120), nullable=False)
    title = db.Column(db.Text, nullable=False)
    updated = db.Column(db.DateTime, default=datetime.datetime.now)
    is_read_daily = db.Column(db.Boolean, default=False)
    articles = db.relationship('Article', backref='site', lazy='dynamic', cascade='all,delete')

    def __repr__(self):
        return "<Site: %s, updated at %s, url: %s>" % (self.title, self.updated, self.url)

class Article(db.Model):
    __tablename__ = 'article'
    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.Text, nullable=False)
    content = db.Column(db.Text, nullable=False)
    updated = db.Column(db.DateTime, default=datetime.datetime.now)
    url = db.Column(db.String(120), nullable=False)
    site_id = db.Column(db.Integer, db.ForeignKey('site.id'))

    def __repr__(self):
        return "<Article: %s, updated at %s>" % (self.title, self.updated)

category_site = db.Table('category_site',
        db.Column('category_id', db.Integer, db.ForeignKey('category.id')),
        db.Column('site_id', db.Integer, db.ForeignKey('site.id'))
        )

class Category(db.Model):
    __tablename__ = 'category'
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(120), nullable=False)
    sites = db.relationship('Site', secondary=category_site, backref=db.backref('categories',
        lazy='dynamic'), lazy='dynamic')

    def __repr__(self):
        return "<Category: %s>" % self.name

class FavArticle(db.Model):
    __tablename__ = 'favarticle'

    id = db.Column(db.Integer, primary_key=True)
    title = db.Column(db.Text, nullable=False)
    content = db.Column(db.Text, nullable=False)
    updated = db.Column(db.DateTime, default=datetime.datetime.now)
    url = db.Column(db.String(120), nullable=False)
    site_id = db.Column(db.Integer, db.ForeignKey('site.id'))

    def __repr__(self):
        return "<FavArticle: %s, updated at %s>" % (self.title, self.updated)
