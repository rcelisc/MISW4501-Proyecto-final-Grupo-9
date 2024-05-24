from flask import Blueprint, request, jsonify
from ..commands.create_user import CreateUserCommandHandler
from ..commands.register_demographic_data import RegisterDemographicDataCommandHandler
from ..commands.register_sports_habits import RegisterSportsHabitDataCommandHandler
from ..commands.update_user_plan import UpdateUserPlanCommandHandler
from ..commands.register_food_data import RegisterFoodDataCommandHandler
from ..middlewares.auth import token_required

user_blueprint = Blueprint('user', __name__)

@user_blueprint.route('/users', methods=['POST'])
def create_user():
    data = request.get_json()
    handler = CreateUserCommandHandler()
    user_type = data.get('type', 'athlete')
    user_id = handler.handle(data, user_type)
    return jsonify({"id": user_id}), 201

@user_blueprint.route('/users/<int:user_id>/demographic_data', methods=['POST'])
@token_required('athlete')
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
    
@user_blueprint.route('/users/<int:user_id>/food_data', methods=['POST'])
@token_required('athlete')
def register_food_data(user_id):
    data = request.get_json()
    handler = RegisterFoodDataCommandHandler()
    try:
        handler.handle(user_id, data)
        return jsonify({"message": "Food data updated successfully"}), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 404
    except Exception as e:
        return jsonify({"error": str(e)}), 400
    
@user_blueprint.route('/users/<int:user_id>/sports_habits', methods=['POST'])
@token_required('athlete')
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
    
@user_blueprint.route('/users/<int:user_id>/plan', methods=['POST'])
@token_required('athlete')
def update_user_plan(user_id):
    data = request.get_json()
    plan_type = data.get('plan_type')
    if plan_type not in ['basic', 'intermediate', 'premium']:
        return jsonify({"error": "Invalid plan type"}), 400
    
    handler = UpdateUserPlanCommandHandler()
    try:
        handler.handle(user_id, plan_type)
        return jsonify({"message": "Plan updated successfully", "plan_type": plan_type}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 400