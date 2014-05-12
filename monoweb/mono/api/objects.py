"""
API object specification and correspond methods
"""

def fill_list_article_object(title, site, updated):
    """
    list article object:
        {
            title: title,
            site: site_title,
            updated: YYYY-MM-DD
        }
    """
    print updated
    result = dict(title=title, site=site, updated=updated.strftime("%Y-%m-%d"))
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
