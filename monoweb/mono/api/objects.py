"""
API object specification and correspond methods
"""

def fill_timeline_article_object(title, site, updated):
    """
    timeline article object:
        {
            title: title,
            site: site_title,
            updated: YYYY-MM-DD
        }
    """
    result = dict(title=title, site=site, updated=updated.strftime("%Y-%M-%d"))
    return result

def fill_article_object():
    pass
