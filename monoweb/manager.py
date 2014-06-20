from flask.ext.script import Manager, Command, Option
from mono import mono_app
from mono.database import db
from mono.models import init_db, update_all_site
from mono.task import start_worker, start_clock
from mono.logger import init_logger

init_logger(mono_app, mono_app.config.get('LOG_FILE', 'monoreader.log'))

import os

manager = Manager(mono_app)

@manager.shell
def shell():
    from mono.models import Site, Article, Category
    return dict(
            app=mono_app,
            db=db,
            use_bpython=True,
            Site=Site,
            Article=Article,
            Category=Category,
            update_all_site=update_all_site
            )

@manager.command
def syncdb():
    from mono.models import Site, Article, Category
    db.create_all()
    init_db(mono_app)

@manager.command
def dropdb():
    db.drop_all()
    db_path = os.path.join(mono_app.config['APP_DIR_NAME'], mono_app.config['DATABASE_NAME'])
    os.remove(db_path)

@manager.command
def runserver():
    mono_app.run(host="0.0.0.0", port=5000)

@manager.command
def worker():
    start_worker()

@manager.command
def clock():
    start_clock()

@manager.command
def gunicorn(host="0.0.0.0", port="5000"):
    os.system("gunicorn -w 4 -b %s:%s wsgi:app" % (host, port))


if __name__ == '__main__':
    manager.run()
