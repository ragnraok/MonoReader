from flask import Flask
from mono.app import init_app
from mono.logger import init_task_logger

def init_task_app():
    app = init_app(is_register_api=False)
    init_task_logger(app, app.config.get('TASK_LOG_FILE', "monoreader_task.log"))

    return app

task_app = init_task_app()
