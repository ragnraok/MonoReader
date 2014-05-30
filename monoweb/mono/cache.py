from flask import current_app

import pickle

CACHE_FILE = "disk_cache"

class SimpleDiskCache(object):
    """
    not thread safe
    """
    def __init__(self):
        self.__cache = {}

    @classmethod
    def create_instance(cls):
        if hasattr(cls, '__instance'):
            return cls.__instance
        else:
            cls.__instance = cls()
            return cls.__instance

    def __setitem__(self, key, value):
        self.__cache[key] = value
        dumps_result = pickle.dumps(self.__cache)
        current_app.logger.info('set key: %s, value: %s' % (key, value))

    def __getitem__(self, key):
        current_app.logger.info("get key: %s, value: %s" % (key, self.__cache.get(key)))
        return self.__cache.get(key)

    def __len__(self):
        return len(self.__cache)

cache = SimpleCache.create_instance()
