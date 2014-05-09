from flask.ext.script import Manager, Command, Option
from mono import mono_app
from mono.database import db

manager = Manager(mono_app)

@manager.shell
def shell():
    from mono.models import Site, Article, Category, FavArticle
    return dict(
            app=mono_app,
            db=db,
            use_bpython=True,
            Site=Site,
            Article=Article,
            Category=Category,
            FavArticle=FavArticle
            )

@manager.command
def syncdb():
    from mono.models import Site, Article, Category, FavArticle
    db.create_all()

@manager.command
def dropdb():
    db.drop_all()

class GunicornServer(Command):

    description = 'Run the app within Gunicorn'

    def __init__(self, host='127.0.0.1', port=5000, workers=4):
        self.port = port
        self.host = host
        self.workers = workers

    def get_options(self):
        return (
            Option('-H', '--host',
                   dest='host',
                   default=self.host),

            Option('-p', '--port',
                   dest='port',
                   type=int,
                   default=self.port),

            Option('-w', '--workers',
                   dest='workers',
                   type=int,
                   default=self.workers),
        )

    def handle(self, app, host, port, workers):

        from gunicorn import version_info

        if version_info < (0, 9, 0):
            from gunicorn.arbiter import Arbiter
            from gunicorn.config import Config
            arbiter = Arbiter(Config({'bind': "%s:%d" % (host, int(port)),'workers': workers}), app)
            arbiter.run()
        else:
            from gunicorn.app.base import Application

            class FlaskApplication(Application):
                def init(self, parser, opts, args):
                    return {
                        'bind': '{0}:{1}'.format(host, port),
                        'workers': workers
                    }

                def load(self):
                    return app

            FlaskApplication().run()


manager.add_command("gunicorn", GunicornServer())


if __name__ == '__main__':
    manager.run()
