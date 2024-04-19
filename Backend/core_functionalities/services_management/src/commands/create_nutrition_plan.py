from google.cloud import pubsub_v1
from ..models.nutrition_plan import NutritionPlan, db
import json

class CreateNutritionPlanCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'nutrition-plan-events')

    def handle(self, data):
        new_plan = NutritionPlan(
            description=data['description'],
            user_id=data['user_id'],
            food_types=data['food_types'],
            meal_frequency=data['meal_frequency'],
            nutritional_objectives=data['nutritional_objectives']
        )
        db.session.add(new_plan)
        db.session.commit()
        message_data = {
            "event_type": "NutritionPlanCreated",
            "plan_id": new_plan.id,
            "user_id": new_plan.user_id
            }
        self.publisher.publish(self.topic_path, json.dumps(message_data).encode('utf-8'))
        return new_plan.id
