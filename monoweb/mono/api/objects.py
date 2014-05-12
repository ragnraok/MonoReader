"""
API object specification and correspond methods
"""

def fill_list_article_object(title, site, updated, cover_url):
    """
    list article object:
        {
            title: title,
            site: site_title,
            updated: YYYY-MM-DD,
            cover_url: url, may be None
        }
    """
    result = dict(title=title, site=site, updated=updated.strftime("%Y-%m-%d"),
            cover_url=cover_url)
    return result

def fill_article_object(title, site, updated, content, url):
    """
    article object:
        {
            title: title,
            site: site_title,
            updated: YYYY-MM-DD,
            content: html content,
            url: origin article url
        }
    """
    return dict(title=title, site=site, updated=updated.strftime("%Y-%M-%d"),
            content=content, url=url)

def fill_site_object(title, updated, url, category, is_read_daily, article_count):
    """
    site object:
        {
            title: title,
            udpated: YYYY-MM-DD,
            category: category,
            is_fav: boolean,
            article_count: article_count,
            url: url
        }
    """
    return dict(title=title, updated=updated.strftime("%Y-%m-%d"), category=getattr(category, 'name', None),
            is_fav=is_read_daily, article_count=article_count, url=url)

def fill_category_object(name):
    """
    category object:
        {
            name: category_name
        }
    """
    return dict(name=name)
