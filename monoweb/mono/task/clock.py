from apscheduler.scheduler import Scheduler
from base import task_app as app

import sys

sched = Scheduler(daemonic=False)

@sched.interval_schedule(minutes=1)
#@sched.cron_schedule(hour='0')
def clock_task():
    app.logger.info("start execute task")
    from worker import add_update_task
    from mono.models import Site

    sites = Site.query.all()
    for site in sites:
        add_update_task(site)

def start_clock():
    sched.start()
