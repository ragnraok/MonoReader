"""
0: server uesrname
"""
SUPERVISOR_TEMPLATE = """
[unix_http_server]
file=/tmp/supervisor.sock                       ; path to your socket file

[supervisord]
logfile=log/supervisord/supervisord.log         ; supervisord log file
logfile_maxbytes=50MB                           ; maximum size of logfile before rotation
logfile_backups=10                              ; number of backed up logfiles
loglevel=error                                  ; info, debug, warn, trace
pidfile=/tmp/supervisord.pid                    ; pidfile location
nodaemon=false                                  ; run supervisord as a daemon
minfds=1024                                     ; number of startup file descriptors
minprocs=200                                    ; number of process descriptors
childlogdir=log/supervisord/                    ; where child log files will live

[rpcinterface:supervisor]
supervisor.rpcinterface_factory = supervisor.rpcinterface:make_main_rpcinterface

[supervisorctl]
serverurl=unix:///tmp/supervisor.sock           ; use a unix:// URL  for a unix socket

[program:mono]
user={username}
command=python manager.py gunicorn --host {gunicorn_host} --port {gunicorn_port}
autostart=false
autorestart=true
directory=.
stderr_logfile=log/mono/mono_stderr.log
stdout_logfile=log/mono/mono_stdout.log

[program:worker]
user={username}
command=python manager.py worker
autostart=false
autorestart=true
directory=.
stderr_logfile=log/task/worker_stderr.log
stdout_logfile=log/task/worker_stdout.log

[program:clock]
user={username}
command=python manager.py clock
autostart=false
autorestart=true
directory=.
stderr_logfile=log/task/clock_stderr.log
stdout_logfile=log/task/clock_stdout.log

[program:redis]
user={username}
command=redis-server
autostart=false
autorestart=true
directory=.
stderr_logfile=log/redis/redis_stderr.log
stdout_logfile=log/redis/redis_stdout.log
"""
