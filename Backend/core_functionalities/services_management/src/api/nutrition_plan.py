from flask import Blueprint, request, jsonify
from ..queries.get_nutrition_plan import GetNutritionPlansQueryHandler
from ..commands.create_nutrition_plan import CreateNutritionPlanCommandHandler

nutrition_blueprint = Blueprint('nutrition', __name__)

@nutrition_blueprint.route('/nutrition-plans', methods=['GET'])
def get_nutrition_plans():
    handler = GetNutritionPlansQueryHandler()
    nutrition_plan_data = handler.handle()
    return jsonify(nutrition_plan_data), 200

@nutrition_blueprint.route('/nutrition-plans', methods=['POST'])
def create_nutrtion_plans():
    handler = CreateNutritionPlanCommandHandler()
    data = request.json
    nutrition_plan_id = handler.handle(data)
    return jsonify({"message": "Nutrition plan created successfully", "nutrition_plan_id": nutrition_plan_id}), 201