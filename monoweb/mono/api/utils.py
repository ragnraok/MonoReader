from flask import jsonify


"""
Error Code Specification:
    0 -> success
    1 -> data format error
    2 -> page small than 1
    3 -> site not exist
"""
SUCCESS = 0
DATA_FORMAT_ERROR = 1
PAGE_SMALL_THAN_ONE = 2
SITE_NOT_EXIST = 3

def make_api_response(error_code, data=None):
    return jsonify(error_code=error_code, data=data)

def get_post_data():
    result = request.get_json(force=True, silent=True)
    if result is None:
        result = request.data
    return result