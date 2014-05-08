from flask import Flask

def init_app(config_file):
    app = Flask(__name__)
    app.config.from_pyfile(config)
    app.debug = app.config.get('DEBUG', True)

    return app
