from flask import Flask

from database import db

def init_app(config_file):
    app = Flask(__name__)
    app.config.from_pyfile(config_file)
    app.debug = app.config.get('DEBUG', True)

    db.app = app;
    db.init_app(app)


    return app
