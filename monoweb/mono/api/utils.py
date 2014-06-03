from flask import jsonify, request


"""
Error Code Specification:
    0 -> success
    1 -> data format error
    2 -> page small than 1
    3 -> site not exist
    4 -> article not exist
"""
SUCCESS = 0
DATA_FORMAT_ERROR = 1
PAGE_SMALL_THAN_ONE = 2
SITE_NOT_EXIST = 3
ARTICLE_NOT_EXIST = 4

def make_api_response(error_code, data=None):
    return jsonify(error_code=error_code, data=data)

def get_post_data():
    result = request.get_json(force=True, silent=True)
    if result is not None:
        return result
    result = request.data
    try:
        """
        all post data must be in json format
        """
        result = json.loads(result)
        return result
    except e:
        print e
        raise ValueError(DATA_FORMAT_ERROR)
