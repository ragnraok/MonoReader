from flask import Flask

from database import db
from api import register_api
from logger import init_logger

def init_app(config_file='config.py', is_register_api=True, is_register_view=True):
    app = Flask(__name__)
    app.config.from_pyfile(config_file)
    app.debug = app.config.get('DEBUG', True)

    db.app = app;
    db.init_app(app)

    if is_register_api:
        register_api(app)

    return app


if __name__ == '__main':
    app = init_app('config.py')
    app.run()
