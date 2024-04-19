from flask import Blueprint, request, jsonify
from ..commands.create_user import CreateUserCommandHandler

user_blueprint = Blueprint('user', __name__)

@user_blueprint.route('/users', methods=['POST'])
def create_user():
    data = request.get_json()
    handler = CreateUserCommandHandler()
    user_type = data.get('type', 'athlete')  # Default to 'athlete' if not specified
    user_id = handler.handle(data, user_type)
    return jsonify({"id": user_id}), 201