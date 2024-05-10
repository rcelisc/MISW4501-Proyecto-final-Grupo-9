from flask import Blueprint, request, jsonify, g
from ..queries.get_user import GetUsersQueryHandler, GetUserByIdQueryHandler
from ..middlewares.auth import token_required

user_blueprint = Blueprint('user', __name__)

@user_blueprint.route('/users/get', methods=['GET'])
@token_required('complementary_services_professional', 'event_organizer')
def get_users():
    user_type = request.args.get('type', 'athlete')
    handler = GetUsersQueryHandler(user_type=user_type)
    users = handler.handle()
    return jsonify(users), 200

@user_blueprint.route('/users/<int:user_id>', methods=['GET'])
@token_required('athlete', 'complementary_services_professional', 'event_organizer')
def get_user(user_id):
    current_user = g.current_user
    # Allow event organizers to view any user's details
    if current_user.type != 'event_organizer' and current_user.id != user_id:
        return jsonify({'message': 'Unauthorized to view this user'}), 403

    handler = GetUserByIdQueryHandler()
    user = handler.handle(user_id)
    if user:
        return jsonify(user), 200
    else:
        return jsonify({'message': 'User not found'}), 404
