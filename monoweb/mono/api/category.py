from flask import current_app

from apibase import BaseAPIGETView, BaseAPIPOSTView
from objects import fill_category_object
from utils import SITE_NOT_EXIST, DATA_FORMAT_ERROR, get_post_data
from mono.models import Site, Category

class CategoryListView(BaseAPIGETView):
    def get_data(self, **kwargs):
        category_list = Category.query.all()
        result = []
        for category in category_list:
            result.append(fill_category_object(name=category.name))
        return result

class CategorySetView(BaseAPIPOSTView):
    def __init__(self, is_set=True, **kwargs):
        super(CategorySetView, self).__init__()
        self.is_set = is_set

    def proc_data(self, data, **kwargs):
        """
        post data format:
            1. set category:
            {
                site_id: site_id,
                category: category_name
            }
            2. unset category
            {
                site_id: site_id
            }
        """
        data = dict(data)
        site_id = data.get('site_id', None)
        category_name = data.get('category', None)
        if site_id is None:
            raise ValueError(DATA_FORMAT_ERROR)
        if self.is_set is True and category_name is None:
            raise ValueError(DATA_FORMAT_ERROR)
        site = Site.query.get(site_id)
        if site is None:
            raise ValueError(SITE_NOT_EXIST)
        if self.is_set:
            category = Category.query.filter_by(name=category_name)
            if category is None:
                category = Category(name=category_name)
                category.save()
            site.set_category(category)
        else:
            site.unset_category()
