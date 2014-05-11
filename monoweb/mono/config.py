import os

HERE = os.path.dirname(__file__)

DEBUG = True
APP_DIR_NAME = "mono"

ARTICLE_NUM_PER_PAGE = 10

# database setting
DATABASE_NAME = 'monoreader.db'
SQLALCHEMY_DATABASE_URI = "sqlite:////%s/%s" % (HERE, DATABASE_NAME)
SQLALCHEMY_ECHO = False

