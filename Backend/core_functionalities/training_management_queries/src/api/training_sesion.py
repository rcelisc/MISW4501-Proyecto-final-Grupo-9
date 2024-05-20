from flask import jsonify, Blueprint
from ..queries.get_training_session import GetTrainingSessionHandler
from ..middlewares.auth import token_required

training_session_blueprint = Blueprint('training_session', __name__)


@training_session_blueprint.route('/training-sessions', methods = ['GET'])
@token_required('complementary_services_professional', 'athlete')
def get_all_training_sessions():
   handler = GetTrainingSessionHandler()
   sessions = handler.handle()
   return jsonify(sessions),200

@training_session_blueprint.route('/training-sessions/<uuid:session_id>', methods=['GET'])
@token_required('complementary_services_professional', 'athlete')
def get_training_session(session_id):
    handler = GetTrainingSessionHandler()
    session = handler.handle(session_id=session_id)
    return jsonify(session), 200

@training_session_blueprint.route('/training-sessions/user/<int:user_id>', methods=['GET'])
@token_required('complementary_services_professional', 'athlete')
def get_training_session_by_user_id(user_id):
    handler = GetTrainingSessionHandler()
    session = handler.handle(user_id=user_id)
    return jsonify(session), 200