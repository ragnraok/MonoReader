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
LOG_FILE = "monoreader.log"
TASK_LOG_FILE = "monoreader_task.log"
