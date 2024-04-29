from google.cloud import pubsub_v1
from ..models.user import db, Athlete
import json

class RegisterDemographicDataCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'demographic-data-events')

    def handle(self, user_id, data):
        user = Athlete.query.get(user_id)
        if not user:
            raise ValueError("User not found")

        # Update user with new demographic data
        user.ethnicity = data.get('ethnicity')
        user.age = data.get('age')
        user.gender = data.get('gender')
        user.heart_rate = data.get('heart_rate')
        user.vo2_max = data.get('vo2_max')
        user.blood_pressure = data.get('blood_pressure')
        user.respiratory_rate = data.get('respiratory_rate')
        db.session.commit()

        # Prepare and publish event data
        event_data = {
            "type": "DemographicDataUpdated",
            "data": {
                "user_id": str(user.id),
                "ethnicity": user.ethnicity,
                "age": user.age,
                "gender": user.gender,
                "heart_rate": user.heart_rate,
                "vo2_max": user.vo2_max,
                "blood_pressure": user.blood_pressure,
                "respiratory_rate": user.respiratory_rate
            }
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))

        return user.id
