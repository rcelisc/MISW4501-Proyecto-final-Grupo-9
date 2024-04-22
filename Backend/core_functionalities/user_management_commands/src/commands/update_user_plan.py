from ..models.user import Athlete, db
from google.cloud import pubsub_v1
import json

class UpdateUserPlanCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'user-plan-updated')

    def handle(self, user_id, plan_type):
        user = Athlete.query.get(user_id)
        if not user:
            raise Exception("User not found")

        user.plan_type = plan_type
        db.session.commit()

        # Publish an event
        event_data = json.dumps({
            "type": "UserPlanUpdated",
            "data": {"user_id": str(user.id), "plan_type": user.plan_type}
        }).encode('utf-8')
        self.publisher.publish(self.topic_path, event_data)
