from rq import Connection, Queue, Worker
from mono.models import Site
from base import task_app as app

import os
import redis

QUEUE_NAME = "MonoReaderQueue"

redis_url = app.config.get('REDIS_URL', "redis://localhost:6379")
connection = redis.from_url(redis_url)

queue = Queue(QUEUE_NAME, connection=connection)

def update_site(site):
    if site:
        site.update_site()

def add_update_task(site):
    app.logger.info("update site %s", site)
    queue.enqueue(update_site, site)

def start_worker():
    with Connection(connection):
        worker = Worker(queues=queue)
        worker.work()
