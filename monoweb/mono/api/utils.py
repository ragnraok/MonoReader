from flask import jsonify


"""
Error Code Specification:
    0 -> success
    2 -> page small than 1
    3 -> site not exist
"""
SUCCESS = 0
PAGE_SMALL_THAN_ONE = 1
SITE_NOT_EXIST = 3

def make_api_response(error_code, data):
    return jsonify(error_code=error_code, data=data)
