from apibase import BaseAPIPOSTView
from utils import DATA_FORMAT_ERROR, SITE_NOT_EXIST
from mono.models import Site, Category

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
            if Site.query.is_exist_by_url(site_url):
                return
            new_site = Site(url=site_url, title=title)
            new_site.save()
            if category is not None:
                new_site.set_category_by_name(category_name)
            else:
                new_site.unset_category()
            new_site.update_site()
        else:
            site_id = data.get('site_id', None)
            if site_id is None:
                raise ValueError(DATA_FORMAT_ERROR)
            site = Site.query.get(site_id)
            if site is None:
                raise ValueError(SITE_NOT_EXIST)
            site.delete()
