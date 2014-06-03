from flask import current_app

from apibase import BaseAPIPOSTView
from utils import DATA_FORMAT_ERROR, SITE_NOT_EXIST
from mono.models import Site, Category
from mono.task.worker import add_update_task
from mono.feed import get_feed_url
from mono import cache

import datetime
import calendar

class SiteSubscribeView(BaseAPIPOSTView):
    def __init__(self, is_subscribe=True, **kwargs):
        super(SiteSubscribeView, self).__init__(**kwargs)
        self.is_subscribe = is_subscribe

    def proc_data(self, data, **kwargs):
        """
        post data format:
            1. subscribe:
                {
                    title: title(optional, may be null)
                    site_url: url,
                    category: category_name(optional, may be null)
                }
            2. unsubscribe:
                {
                    site_id: id
                }
        """
        if self.is_subscribe:
            site_url = data.get('site_url', None)
            category = data.get('category', None)
            title = data.get('title', None)
            if site_url is None:
                raise ValueError(DATA_FORMAT_ERROR)
            feed_url = get_feed_url(site_url)
            if feed_url is None:
                current_app.logger.error("subscribe error, cannot get feed url for site %s" % site_url)
                raise ValueError(SITE_NOT_EXIST)
            site = Site.query.get_by_url(feed_url)
            if site is not None:
                site.is_subscribe = True
                site.save()
                return
            new_site = Site(url=feed_url, title=title, origin_url=site_url)
            new_site.save()
            if category is not None:
                new_site.set_category_by_name(category_name)
            else:
                new_site.unset_category()

            current_app.logger.info("subscribe site: %s" % new_site.url)
            #new_site.update_site()
            add_update_task(new_site)
            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            cache[current_app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp
        else:
            site_id = data.get('site_id', None)
            if site_id is None:
                raise ValueError(DATA_FORMAT_ERROR)
            site = Site.query.get(site_id)
            if site is None:
                raise ValueError(SITE_NOT_EXIST)
            site.is_subscribe = False
            site.save()
            now_timestamp = calendar.timegm(datetime.datetime.utcnow().utctimetuple())
            cache[current_app.config['MAIN_TIMELINE_UPDATE_CACHE_KEY']] = now_timestamp
