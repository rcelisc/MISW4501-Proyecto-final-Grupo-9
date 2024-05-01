from google.cloud import pubsub_v1
from ..models.user import db, Athlete
import json

class RegisterFoodDataCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'food-data-events')

    def handle(self, user_id, data):
        user = Athlete.query.get(user_id)
        if not user:
            raise ValueError("User not found")

        # Update user with new demographic data
        user.daily_calories = data.get('daily_calories')
        user.daily_protein = data.get('daily_protein')
        user.daily_liquid = data.get('daily_liquid')
        user.daily_carbs = data.get('daily_carbs')
        user.meal_frequency = data.get('meal_frequency')
        db.session.commit()

        # Prepare and publish event data
        event_data = {
            "type": "FoodDataUpdated",
            "data": {
                "user_id": str(user.id),
                "daily_calories": user.daily_calories,
                "daily_protein": user.daily_protein,
                "daily_liquid": user.daily_liquid,
                "daily_carbs": user.daily_carbs,
                "meal_frequency": user.meal_frequency
            }
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))

        return user.id
