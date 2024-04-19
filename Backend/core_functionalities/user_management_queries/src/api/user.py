from flask import Blueprint, request, jsonify
from ..queries.get_user import GetUsersQueryHandler, GetUserByIdQueryHandler

user_blueprint = Blueprint('user', __name__)

@user_blueprint.route('/users/get', methods=['GET'])
def get_users():
    handler = GetUsersQueryHandler()
    users = handler.handle()
    return jsonify(users), 200

@user_blueprint.route('/users/<int:user_id>', methods=['GET'])
def get_user(user_id):
    handler = GetUserByIdQueryHandler()
    user = handler.handle(user_id)
    return jsonify(user), 200
