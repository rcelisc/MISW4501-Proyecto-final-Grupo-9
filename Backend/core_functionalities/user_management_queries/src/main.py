from flask import Flask
from flask_socketio import SocketIO
from .extensions import db, migrate
from .api.user import user_blueprint
from .config import DevelopmentConfig, TestingConfig, ProductionConfig
from .queries.listen_user import start_listener_in_background
from .queries.listen_nutrition_plan import start_nutrition_plan_listener
from .queries.listen_demographic_data import start_listener_in_background as start_demographic_data_listener
from .queries.listen_sports_habits import start_listener_in_background as start_sports_habits_listener
from .queries.listen_update_user_plan import start_listener_in_background as start_update_user_plan_listener
from .queries.listen_food_data import start_listener_in_background as start_food_data_listener
from .queries.socket_events import setup_socket_events
from flask_cors import CORS
import os

socketio = SocketIO(cors_allowed_origins="http://localhost:4200")

def create_app(config_class=DevelopmentConfig):
    app = Flask(__name__)
    env = os.environ.get('FLASK_ENV', 'development')
    if env == 'production':
        app.config.from_object(ProductionConfig)
    elif env == 'testing':
        app.config.from_object(TestingConfig)
    else:
        app.config.from_object(DevelopmentConfig)
    
    db.init_app(app)
    migrate.init_app(app, db)

    app.register_blueprint(user_blueprint)
    start_listener_in_background(app)
    start_demographic_data_listener(app)
    start_food_data_listener(app)
    start_sports_habits_listener(app)
    start_update_user_plan_listener(app)
    start_nutrition_plan_listener(app, socketio)

    socketio.init_app(app)
    CORS(app)
    return app

if __name__ == "__main__":
    app = create_app()
    socketio.run(app, host="0.0.0.0", port=3007)