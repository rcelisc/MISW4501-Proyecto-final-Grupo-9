from flask import Flask, jsonify
from .extensions import db, migrate
from .api.training_plan import training_api, training_plan_blueprint
from .api.training_sesion import training_session_blueprint
from .config import DevelopmentConfig, ProductionConfig, TestingConfig
from .queries.listen_training import start_listener_in_background
from .queries.listen_metrics import start_listener_in_background as start_listener_in_background_metrics
from .queries.listen_plan import start_listener_in_background as start_listener_in_background_plan
from .models.training_session import TrainingSession
import os
from flask_cors import CORS

def create_app(config_class=DevelopmentConfig):
    app = Flask(__name__)
    env = os.environ.get('FLASK_ENV', 'development')
    if env == 'production':
        app.config.from_object(ProductionConfig)
    elif env == 'testing':
        app.config.from_object(TestingConfig)
    else:
        app.config.from_object(DevelopmentConfig)

    print("Environment:", env)
    print("Database URI:", app.config['SQLALCHEMY_DATABASE_URI'])

    db.init_app(app)
    migrate.init_app(app, db)

    app.register_blueprint(training_api)
    app.register_blueprint(training_plan_blueprint)
    app.register_blueprint(training_session_blueprint)
    from .models.training_session import TrainingSession
    start_listener_in_background(app)
    start_listener_in_background_metrics(app)
    start_listener_in_background_plan(app)
    CORS(app)
    return app

if __name__ == '__main__':
    
    app = create_app()

    app.run(host="0.0.0.0", port=3003)
