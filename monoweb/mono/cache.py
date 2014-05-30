from flask import current_app

import pickle
import os
import time
import fcntl

class FileLock(object):
    def __init__(self, filename, *args, **kwargs):
        self.filename = filename
        self.open_args = args
        self.open_kwargs = kwargs
        self.fileobj = None

    def __enter__(self):
        f = open(self.filename, *self.open_args, **self.open_kwargs)
        while True:
            fcntl.flock(f, fcntl.LOCK_EX)
            fnew = open(self.filename, *self.open_args, **self.open_kwargs)
            if os.path.sameopenfile(f.fileno(), fnew.fileno()):
                fnew.close()
                break
            else:
                f.close()
                f = fnew
        self.fileobj = f
        return f

    def __exit__(self, _exc_type, _exc_value, _trackback):
        self.fileobj.close()

CACHE_FILE = "disk_cache"

class SimpleCache(object):
    """
    a simple dick cache with file lock
    """
    def __init__(self):
        if not os.path.exists(CACHE_FILE):
            f = open(CACHE_FILE, "w")
            f.write(pickle.dumps({"testCache": "testCache"}))
            f.close()

    @classmethod
    def create_instance(cls):
        if hasattr(cls, '__instance'):
            return cls.__instance
        else:
            cls.__instance = cls()
            return cls.__instance

    def __setitem__(self, key, value):
        #self.__cache[key] = value
        with FileLock(CACHE_FILE, "r+") as f:
            cache = ''.join(f.readlines())
            cache = pickle.loads(cache)
            cache[key] = value
            dumps_result = pickle.dumps(cache)
            f.seek(0)
            f.write(dumps_result)
            f.flush()
            current_app.logger.info('set key: %s, value: %s' % (key, value))

    def __getitem__(self, key):
        with FileLock(CACHE_FILE, "r") as f:
            cache = ''.join(f.readlines())
            cache = pickle.loads(cache)
            current_app.logger.info("get key: %s, value: %s" % (key, cache.get(key)))
            return cache.get(key)

    def __len__(self):
        return len(self.__cache)

cache = SimpleCache.create_instance()
