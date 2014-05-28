from feed import Site, Article, Category, FavArticle, TestModel

def init_db(app):
    unclassified_name = app.config.get('UNCLASSIFIED', "not classified")
    if Category.query.filter_by(name=unclassified_name).first() is None:
        unclassified = Category(name=unclassified_name)
        unclassified.save()

def update_all_site():
    sites = Site.query.all()
    for site in sites:
        site.update_site()
