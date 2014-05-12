from feed import Site, Article, Category, FavArticle

def init_db(app):
    unclassified_name = app.config.get('UNCLASSIFIED', "not classified")
    if Category.query.filter_by(name=unclassified_name).first() is None:
        unclassified = Category(name=unclassified_name)
        unclassified.save()
