from rq import Connection, Queue, Worker
from mono.models import Site
from mono import mono_app

import os
import redis

QUEUE_NAME = "MonoReaderQueue"

redis_url = mono_app.config.get('REDIS_URL', "redis://localhost:6379")
connection = redis.from_url(redis_url)

queue = Queue(QUEUE_NAME, connection=connection)

def update_site(site):
    if site:
        site.update_site()

def add_update_task(site):
    print "update task for %s" % site
    queue.enqueue(update_site, site)

def start_worker():
    with Connection(connection):
        worker = Worker(queues=queue)
        worker.work()
