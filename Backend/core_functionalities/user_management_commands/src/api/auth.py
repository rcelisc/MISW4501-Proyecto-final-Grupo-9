from flask import Blueprint, request, jsonify
from ..commands.login_user import LoginUserCommandHandler

auth_blueprint = Blueprint('auth', __name__)

@auth_blueprint.route('/login', methods=['POST'])
def login():
    data = request.get_json()
    id_number = data.get('id_number')
    password = data.get('password')

    # Instantiate the command handler
    command_handler = LoginUserCommandHandler()
    result = command_handler.handle_login(id_number, password)
    
    if result['status']:
        return jsonify({'token': result['token']}), 200
    else:
        return jsonify({'message': result['message']}), 401
