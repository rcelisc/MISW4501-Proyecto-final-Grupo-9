from flask import jsonify, request, Blueprint, Response
from ..queries.get_training_history import GetTrainingHistory
from ..queries.get_training_plan import GetTrainingPlanQueryHandler, GetTrainingPlansByProfileQueryHandler
from ..models.training_history import TrainingHistorySchema
from ..middlewares.auth import token_required

training_history_schema = TrainingHistorySchema()
training_api = Blueprint('training', __name__)
training_plan_blueprint = Blueprint('training_plan', __name__)


@training_api.route('/api/training/history', methods = ['GET'])
def get_training_history():
   result = GetTrainingHistory().execute()
   return jsonify(result),200


@training_plan_blueprint.route('/training-plan/<int:plan_id>', methods=['GET'])
@token_required('complementary_services_professional')
def get_training_plan(plan_id):
    handler = GetTrainingPlanQueryHandler()
    plan = handler.handle(plan_id)
    return jsonify(plan), 200

@training_plan_blueprint.route('/training-plans/<profile>', methods=['GET'])
@token_required('complementary_services_professional', 'athlete')
def get_training_plans_by_profile(profile):
    handler = GetTrainingPlansByProfileQueryHandler()
    training_plans = handler.handle(profile)
    return jsonify(training_plans), 200