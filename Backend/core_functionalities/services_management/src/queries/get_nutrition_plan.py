from ..models.nutrition_plan import NutritionPlan

class GetNutritionPlansQueryHandler:
    def handle(self):
        nutrition_plans = NutritionPlan.query.all()
        nutrition_plans_data = [{
            'id': nutrition_plan.id,
            'description': nutrition_plan.description,
            'user_id': nutrition_plan.user_id,
            'food_types': nutrition_plan.food_types,
            'meal_frequency': nutrition_plan.meal_frequency,
            'nutritional_objectives': nutrition_plan.nutritional_objectives
        } for nutrition_plan in nutrition_plans]
        return nutrition_plans_data