#-*- coding: utf-8 -*-
from HTMLParser import HTMLParser

import requests
import re
import os
import urlparse

class FeedHtmlParser(HTMLParser):
    def __init__(self):
        HTMLParser.__init__(self)
        self.found = False
        self.feed_url = None

    def handle_starttag(self, tag, attrs):
        if tag == 'link' and self.found is False:
            names = [val[0] for val in attrs]
            values = [val[1] for val in attrs]
            if 'type' in names:
                type_index = names.index('type')
                val = values[type_index]
                if 'rss' in val or 'atom' in val:
                    href_index = names.index('href')
                    self.feed_url = values[href_index]
                    self.found = True

    def handle_data(self, data):
        pass

class ArticleImageURLParser(HTMLParser):
    """
    get the first image in the html
    """
    def __init__(self):
        HTMLParser.__init__(self) # old-style class
        self.found = False
        self.image_url = None

    def handle_starttag(self, tag, attrs):
        if tag == 'img' and self.found is False:
            names = [val[0] for val in attrs]
            values = [val[1] for val in attrs]
            if 'src' in names:
                src_index = names.index('src')
                val = values[src_index]
                self.image_url = val
                self.found = True

    def handle_data(self, data):
        pass

def pretreate_html(html):
    # remove all suck tags in html'
    re_judge1 = re.compile('<!-*[^>]*>') # remove browser judgement
    tmp1 = re_judge1.sub('', html)
    tmp2 = tmp1.replace('<![endif]–>', '')
    re_script = re.compile('<\s*script[^>]*>[^<]*<\s*/\s*script\s*>',re.I)
    result = re_script.sub('', tmp2)
    return result

def is_feed_url(url):
    #if 'feed' in url or 'rss' in url or 'atom' in url:
    #    return True
    headers = {'User-Agent': 'Mozilla/5.0 (X11; U; Linux i686)' +
                        'Gecko/20071127 Firefox/2.0.0.11'}
    response = requests.get(url, headers=headers)
    content_type = response.headers.get('content-type', None)
    if 'xml' in content_type:
        return True
    return False

def get_feed_url(url):
    if not url.startswith("http://") and not url.startswith("https://"):
        return None
    try:
        if is_feed_url(url):
            return url
    except:
        return None

    headers = {'User-Agent': 'Mozilla/5.0 (X11; U; Linux i686)' +
                        'Gecko/20071127 Firefox/2.0.0.11'}
    response = requests.get(url, headers=headers)
    html = response.text
    #html = pretreate_html(html.encode('utf-8'))
    parser = FeedHtmlParser()
    parser.feed(html)

    if parser.feed_url:
        feed_url = parser.feed_url
        if 'feedburner' in feed_url:
            feed_url = feed_url + "?fml=xml"
            return feed_url
        elif feed_url.startswith('/'):
            p = urlparse.urlparse(feed_url)
            if p is None or p.hostname is None:
                return None
            url = 'http://' + p.hostname
            feed_url = os.path.join(url, feed_url[1:])
        return feed_url
    else:
        return None

if __name__ == '__main__':
    #link = raw_input("please input a url: ")
    #feed_url = get_feed_url(link)
    #if feed_url:
    #    print feed_url
    #else:
    #    print "cannot find the feed site"
    import requests
    rv = requests.get("http://coolshell.cn/articles/11466.html")
    parser = ArticleImageURLParser()
    parser.feed(rv.text)
    print parser.image_url
