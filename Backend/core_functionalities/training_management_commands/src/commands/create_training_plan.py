from google.cloud import pubsub_v1
from ..models.training_plan import TrainingPlan, db
import json

class CreateTrainingPlanCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'training-plan-events')  

    def handle(self, data):
        plan = TrainingPlan(**data)
        db.session.add(plan)
        db.session.commit()

        event_data = {
            "type": "TrainingPlanCreated",
            "data": {
                "id": str(plan.id),
                "description": plan.description,
                "exercises": plan.exercises,
                "duration": plan.duration,
                "frequency": plan.frequency,
                "objectives": plan.objectives,
            }
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))
        return plan.id
