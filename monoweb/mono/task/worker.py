from rq import Connection, Queue, Worker
from mono.models import Site
from mono import cache
from base import task_app as app

import os
import redis
import datetime
import calendar

QUEUE_NAME = "MonoReaderQueue"

redis_url = app.config.get('REDIS_URL', "redis://localhost:6379")
connection = redis.from_url(redis_url)

queue = Queue(QUEUE_NAME, connection=connection)

def update_site(site):
    if site and site.is_subscribe:
        is_new_article = site.update_site()
        if is_new_article:
            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            key = app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']
            cache[key] = now_timestamp
            if site.is_read_daily:
                key = app.config['FAV_TIMELINE_UPDATE_CACHE_KEY'] = now_timestamp

def add_update_task(site):
    app.logger.info("update site %s", site)
    queue.enqueue(update_site, site)

def start_worker():
    with Connection(connection):
        worker = Worker(queues=queue)
        worker.work()
