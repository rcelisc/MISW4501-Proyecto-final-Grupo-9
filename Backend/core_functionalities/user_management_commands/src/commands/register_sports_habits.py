from google.cloud import pubsub_v1
from ..models.user import db, Athlete
import json

class RegisterSportsHabitDataCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'sports-habit-events')

    def handle(self, user_id, data):
        user = Athlete.query.get(user_id)
        if not user:
            raise ValueError("User not found")

        # Update user with new sports habit data
        user.training_frequency = data.get('training_frequency')
        user.sports_practiced = data.get('sports_practiced')
        user.average_session_duration = data.get('average_session_duration')
        user.recovery_time = data.get('recovery_time')
        user.training_pace = data.get('training_pace')
        db.session.commit()

        # Prepare and publish event data
        event_data = {
            "type": "SportsHabitDataUpdated",
            "data": {
                "user_id": str(user.id),
                "training_frequency": user.training_frequency,
                "sports_practiced": user.sports_practiced,
                "average_session_duration": user.average_session_duration,
                "recovery_time": user.recovery_time,
                "training_pace": user.training_pace
            }
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))

        return user.id
