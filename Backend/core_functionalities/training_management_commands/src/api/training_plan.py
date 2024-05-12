from flask import Blueprint, request, jsonify
from ..commands.create_training_plan import CreateTrainingPlanCommandHandler
from ..middlewares.auth import token_required

training_plan_blueprint = Blueprint('training_plan', __name__)

@training_plan_blueprint.route('/training-plan', methods=['POST'])
@token_required('complementary_services_professional')
def create_training_plan():
    data = request.json
    handler = CreateTrainingPlanCommandHandler()
    training_plan_id = handler.handle(data)
    return jsonify({ "id": training_plan_id }), 201
