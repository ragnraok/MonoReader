from flask import jsonify


"""
Error Code Specification:
    0 -> success
"""

def make_api_response(error_code, data):
    return jsonify(error_code=error_code, data=data)
