from flask import Flask

from database import db
from api import register_api

def init_app(config_file):
    app = Flask(__name__)
    app.config.from_pyfile(config_file)
    #app.debug = app.config.get('DEBUG', True)
    app.debug = True

    db.app = app;
    db.init_app(app)

    register_api(app)

    return app
