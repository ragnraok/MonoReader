from urlparser import get_feed_url

import feedparser
import datetime
import locale

class FeedDataFetcher(object):
    def __init__(self, url, is_feed_url=False):
        self.url = url
        self.is_feed_url = is_feed_url
        self.parser = None

        self.site_title = None
        self.site_updated_time = None
        self.articles = None

    def __init_parser(self):
        locale.setlocale(locale.LC_TIME, "en_US.UTF8")
        if self.is_feed_url == False:
            self.url = get_feed_url(self.url)

        if self.url is not None:
            self.parser = feedparser.parse(self.url)

    def fetch_site_title(self):
        if self.site_title is not None:
            return self.site_title
        elif self.parser is None:
            self.__init_parser()

        if 'title' in self.parser['channel']:
            self.site_title = self.parser['channel']['title']
            return self.site_title
        elif 'subtitle' in self.parser['channel']:
            self.site_title = self.parser['channel']['subtitle']
            return self.site_title

    def fetch_site_updated_time(self):
        if self.site_updated_time is not None:
            return self.site_updated_time
        elif self.parser is None:
            self.__init_parser()
        if 'updated' in self.parser['channel']:
            timestr = self.parser['channel']['updated']
            version = self.parser.version
            self.site_updated_time = self.__parse_timestr(version, timestr,
                    self.url)
        elif 'published' in self.parser['channel']:
            timestr = self.parser['channel']['published']
            version = self.parser.version
            self.site_updated_time = self.__parse_timestr(version, timestr,
                    self.url)
        else:
            self.site_updated_time = datetime.datetime.now()

        return None

    def __parse_timestr(self, version, timestr, url):
        if 'rss' in version:
            try:
                """
                for normal case:
                    timestr[:25] in this format:
                    %a, %d %b %Y %H:%M:%S
                """
                return datetime.datetime.strptime(timestr[:25], "%a, %d %b %Y %H:%M:%S")
            except:
                # some edge cases
                if 'csdn.net' in url:
                    return datetime.datetime.strptime(timestr, "%Y-%m-%d %H:%M:%S")
                elif '163.com' in url:
                    return datetime.datetime.strptime(timestr[:19], "%Y-%m-%dT%H:%M:%S")
                else:
                    try:
                        """
                        in this format:
                            %Y-%m-%d %H:%M:%S
                        """
                        return datetime.datetime.strptime(timestr, "%Y-%m-%d %H:%M:%S")
                    except:
                        """
                        may be in this format timestr[:24]:
                            Tue Aug 20 2013 04:04:46
                        """
                        return datetime.datetime.strptime(timestr[:24], "%a %b %d %Y %H:%M:%S")
        elif 'atom' in version:
            """
            for normal case
                timestr[:19] in this format:
                %Y-%m-%dT%H:%M:%S
            """
            return datetime.datetime.strptime(timestr[:19], "%Y-%m-%dT%H:%M:%S")

    def fetch_articles(self):
        """
        return articles format:
            [{
                'title': title
                'url': original post's url
                'content': content in html
                'date': a datetime.datetime object
            }]
        """
        if self.articles is not None:
            return self.articles
        elif self.parser is None:
            self.__init_parser()

        if 'atom' in self.parser.version:
            item_list = self.parser['items']
            result = []
            for item in item_list:
                article = {}
                article['url'] = item['link']
                article['title'] = item['title']
                article['date'] = self.__parse_timestr(self.parser.version,
                       item['udpated'], self.url)
                article['content'] = item['description']
                result.append(article)
            self.articles = result
        elif 'rss' in self.parser.version:
            item_list = self.parser['items']
            result = []
            for item in item_list:
                article = {}
                article['url'] = item['link']
                article['title'] = item['title']
                article['date'] = self.__parse_timestr(self.parser.version,
                        item['updated'], self.url)
                if 'content' in item:
                    article['content'] = item['content']
                elif 'summary' in item:
                    article['content'] = item['summary']
                elif 'description' in item:
                    article['content'] = item['description']
                result.append(article)
            self.articles = result

        return self.articles
