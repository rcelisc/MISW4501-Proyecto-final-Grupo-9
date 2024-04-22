from flask import Blueprint, request, jsonify
from ..commands.create_user import CreateUserCommandHandler
from ..commands.register_demographic_data import RegisterDemographicDataCommandHandler
from ..commands.register_sports_habits import RegisterSportsHabitDataCommandHandler

user_blueprint = Blueprint('user', __name__)

@user_blueprint.route('/users', methods=['POST'])
def create_user():
    data = request.get_json()
    handler = CreateUserCommandHandler()
    user_type = data.get('type', 'athlete')
    user_id = handler.handle(data, user_type)
    return jsonify({"id": user_id}), 201

@user_blueprint.route('/users/<int:user_id>/demographic_data', methods=['POST'])
def register_demographic_data(user_id):
    data = request.get_json()
    handler = RegisterDemographicDataCommandHandler()
    try:
        handler.handle(user_id, data)
        return jsonify({"message": "Demographic data updated successfully"}), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 400
    
@user_blueprint.route('/users/<int:user_id>/sports_habits', methods=['POST'])
def register_sports_habits(user_id):
    data = request.get_json()
    handler = RegisterSportsHabitDataCommandHandler()
    try:
        handler.handle(user_id, data)
        return jsonify({"message": "Sports habit data updated successfully"}), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 400