from deploy_config import *
from mono.config import LOG_DIRS
from supervisor_template import SUPERVISOR_TEMPLATE

from fabric.api import *

import os

env.hosts = HOST

src_dir = "MonoReader"

def create_supervisord_file():
    supervisord_config = SUPERVISOR_TEMPLATE.format(HOST_UESRNAME)
    supervisord_file = open(SUPERVISOR_FILENAME, "w")
    supervisord_file.write(supervisord_config)
    supervisord_file.close()

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
    # install supervisor
    # install pip requirements
    run("pip install -r requirements.txt")

def prepare():
    with cd("$HOME"):
        get_code()
        with cd(WEB_PROJECT_DIR):
            # make virtualenv
            pull()
            run("virtualenv venv")
            run("source venv/bin/activate")
            install_requirements()
            create_supervisord_file()

def config_nginx():
    pass

def deploy():
    # enter virtualenv
    with cd(os.path.join("$HOME", WEB_PROJECT_DIR)):
        # create log dir
        for _dir in LOG_DIRS:
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
