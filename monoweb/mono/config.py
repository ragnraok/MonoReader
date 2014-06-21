import os

HERE = os.path.dirname(__file__)

DEBUG = True
APP_DIR_NAME = "mono"

ARTICLE_NUM_PER_PAGE = 10
API_URL_PREFIX = "/api"
UNCLASSIFIED = "not classified"

# database setting
DATABASE_NAME = 'monoreader.db'
SQLALCHEMY_DATABASE_URI = "sqlite:////%s/%s" % (HERE, DATABASE_NAME)
SQLALCHEMY_ECHO = False

# redis
REDIS_URL = "redis://localhost:6379"

# logging
ROOT_LOG_DIR = "log"
MONO_LOG_DIR = os.path.join(ROOT_LOG_DIR, "mono")
TASK_LOG_DIR = os.path.join(ROOT_LOG_DIR, "task")
REDIS_LOG_DIR = os.path.join(ROOT_LOG_DIR, "redis")
SUPERVISOR_LOG_DIR = os.path.join(ROOT_LOG_DIR, "supervisord")
LOG_FILE = os.path.join(MONO_LOG_DIR, "monoreader.log") #"log/mono/monoreader.log"
TASK_LOG_FILE = os.path.join(TASK_LOG_DIR, "monoreader_task.log") #"log/task/monoreader_task.log"

LOG_DIRS = [ROOT_LOG_DIR, MONO_LOG_DIR, TASK_LOG_DIR, REDIS_LOG_DIR, SUPERVISOR_LOG_DIR]

# cache
MAIN_TIMELINE_UPDATE_CACHE_KEY = "timeline_update"
FAV_TIMELINE_UPDATE_CACHE_KEY = "fav_timeline_udpate"
FAV_ARTICLE_LIST_UPDATE_CACHE_KEY = "fav_article_list_update"
