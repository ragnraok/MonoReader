"""
API object specification and correspond methods
"""

def fill_list_article_object(article_id, title, site, updated, cover_url):
    """
    list article object:
        {
            article_id: article_id,
            title: title,
            site: site_title,
            updated: YYYY-MM-DD,
            cover_url: url, may be None
        }
    """
    result = dict(article_id=article_id, title=title, site=site,
            updated=updated.strftime("%Y-%m-%d"), cover_url=cover_url)
    return result

def fill_article_object(article_id, title, site, updated, content, url, cover_url):
    """
    article object:
        {
            article_id: article_id,
            title: title,
            site: site_title,
            updated: YYYY-MM-DD,
            content: html content,
            url: origin article url,
            cover_url: url, may be null
        }
    """
    return dict(article_id=article_id, title=title, site=site,
            updated=updated.strftime("%Y-%M-%d"), content=content, url=url,
            cover_url=cover_url)

def fill_site_object(site_id, title, updated, url, category, is_read_daily, article_count):
    """
    site object:
        {
            site_id: site_id,
            title: title,
            udpated: YYYY-MM-DD,
            category: category,
            is_fav: boolean,
            article_count: article_count,
            url: url
        }
    """
    return dict(site_id=site_id, title=title, updated=updated.strftime("%Y-%m-%d"),
            category=getattr(category, 'name', None),
            is_fav=is_read_daily, article_count=article_count, url=url)

def fill_category_object(category_id, name, is_un_classified):
    """
    category object:
        {
            category_id: category_id,
            name: category_name,
            is_un_classified: boolean
        }
    """
    return dict(category_id=category_id, name=name, is_un_classified=is_un_classified)
