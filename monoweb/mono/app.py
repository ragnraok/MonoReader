from flask import Flask

from database import db
from api import register_api
from models import init_db

def init_app(config_file):
    app = Flask(__name__)
    app.config.from_pyfile(config_file)
    #app.debug = app.config.get('DEBUG', True)
    app.debug = True

    db.app = app;
    db.init_app(app)

    init_db(app)
    register_api(app)

    return app
