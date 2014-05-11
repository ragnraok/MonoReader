from urlconfig import config_api_url, api_app

def register_api(app):
    config_api_url()
    app.register_blueprint(api_app, url_prefix=app.config.get("API_URL_PREFIX"))
