import os

HERE = os.path.dirname(__file__)

DEBUG = True

# database setting
DATABASE_NAME = 'monoreader.db'
SQLALCHEMY_DATABASE_URI = "sqlite:////%s/%s" % (HERE, DATABASE_NAME)
if DEBUG:
    SQLALCHEMY_ECHO = True
else:
    SQLALCHEMY_ECHO = False

