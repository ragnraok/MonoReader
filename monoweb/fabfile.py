from deploy_config import *
from supervisor_template import SUPERVISOR_TEMPLATE

from fabric.api import *

import os

env.user = HOST_UESRNAME
env.password = HOST_PASSWORD
env.hosts = HOST

src_dir = "MonoReader"

def sys_info():
    run("uname -a")

def create_supervisord_file():
    supervisord_config = SUPERVISOR_TEMPLATE.format(username=HOST_UESRNAME, gunicorn_host=GUNICORN_HOST,
            gunicorn_port=GUNICORN_PORT)
    supervisord_file = open(SUPERVISOR_FILENAME, "w")
    supervisord_file.write(supervisord_config)
    supervisord_file.close()
    supervisord_file = open(SUPERVISOR_FILENAME)
    put(supervisord_file, SUPERVISOR_FILENAME, use_sudo=True)

def get_code():
    run("git clone %s" % GIT_URL)

def pull():
    run("git pull")

def install_requirements():
    # install redis
    run("curl -OL http://download.redis.io/releases/redis-2.8.9.tar.gz")
    run("tar -xvzf redis-2.8.9.tar.gz")
    with cd("redis-2.8.9"):
        run("make -j4")
        run("make install")
    run("rm -r redis-2.8.9.tar.gz redis-2.8.9")
    # install pip
    run("curl -OL https://pypi.python.org/packages/source/p/pip/pip-1.5.6.tar.gz")
    run("tar -xvzf pip-1.5.6.tar.gz")
    with cd("pip-1.5.6"):
        sudo("python setup.py install")
    run("rm -rf pip-1.5.6 pip-1.5.6.tar.gz")
    # install supervisor
    run("curl -OL https://pypi.python.org/packages/source/s/supervisor/supervisor-3.0.tar.gz")
    run("tar -xvzf supervisor-3.0.tar.gz")
    with cd("supervisor-3.0"):
        sudo("python setup.py install")
    run("rm -rf supervisor-3.0 supervisor-3.0.tar.gz")
    # install pip requirements
    sudo("pip install -r requirements.txt")

def prepare():
    with cd("$HOME"):
        with settings(warn_only=True):
            if run("test -d %s" % PROJECT_DIR).failed:
                get_code()
                with cd(WEB_PROJECT_DIR):
                    # make virtualenv
                    pull()
                    run("virtualenv venv")
                    run("source venv/bin/activate")
                    install_requirements()
                    create_supervisord_file()
                    run("python manager.py syncdb")
                    return
            else:
                with cd(WEB_PROJECT_DIR):
                    run("source venv/bin/activate")
                    create_supervisord_file()
                    pull()
                    run("python manager.py syncdb")

def config_nginx():
    pass

def deploy():
    # enter virtualenv
    with cd(os.path.join("$HOME", WEB_PROJECT_DIR)):
        # create log dir
        with settings(warn_only=True):
            for _dir in LOG_DIRS:
                if run("test -d %s" % _dir).failed:
                    run("mkdir %s" % _dir)
        # enter virtualenv
        run("source venv/bin/activate")
        # start server
        run("supervisord -c %s" % SUPERVISOR_FILENAME)
        run("supervisorctl reread")
        run("supervisorctl update")
        run("supervisorctl start mono")
        run("supervisorctl start redis")
        run("supervisorctl start worker")
        run("supervisorctl start clock")

def stop():
    with cd(os.path.join("$HOME", WEB_PROJECT_DIR)):
        # enter virtualenv
        run("source venv/bin/activate")

        run("supervisorctl stop mono")
        run("supervisorctl stop redis")
        run("supervisorctl stop worker")
        run("supervisorctl stop clock")

def restart():
    with cd(os.path.join("$HOME", WEB_PROJECT_DIR)):
        # enter virtualenv
        run("source venv/bin/activate")

        run("supervisorctl restart mono")
        run("supervisorctl restart redis")
        run("supervisorctl restart worker")
        run("supervisorctl restart clock")

def status():
    with cd(os.path.join("$HOME", WEB_PROJECT_DIR)):
        # enter virtualenv
        run("source venv/bin/activate")

        run("supervisorctl status mono")
        run("supervisorctl status worker")
        run("supervisorctl status clock")

#def test():
#    with cd(os.path.join("$HOME", WEB_PROJECT_DIR)):
#        run("source venv/bin/activate")
#        run("python test/test_site.py")
