from apscheduler.scheduler import Scheduler

import sys

sched = Scheduler(daemonic=False)

@sched.interval_schedule(minutes=1)
#@sched.cron_schedule(hour='0')
def clock_task():
    print "exeute task"
    from worker import add_update_task
    from mono.models import Site

    sites = Site.query.all()
    for site in sites:
        add_update_task(site)

def start_clock():
    print "start clock"
    sched.start()
