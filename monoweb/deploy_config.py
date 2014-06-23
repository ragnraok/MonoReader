from mono.config import LOG_DIRS

import os

HOST = [""]
HOST_UESRNAME = ""
HOST_PASSWORD = ""
GIT_URL = "https://github.com/ragnraok/MonoReader.git"
SUPERVISOR_FILENAME = "supervisord.conf"
PROJECT_DIR = "MonoReader"
WEB_PROJECT_DIR = "MonoReader/monoweb"
GLOBAL_WEB_PROJECT_DIR = os.path.join("$HOME", WEB_PROJECT_DIR)
GUNICORN_HOST = "127.0.0.1"
GUNICORN_PORT = "6000"
