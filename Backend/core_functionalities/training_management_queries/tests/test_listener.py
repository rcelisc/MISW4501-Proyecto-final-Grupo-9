import unittest
from unittest.mock import patch, MagicMock
from google.cloud.pubsub_v1 import SubscriberClient
from google.cloud import pubsub_v1
from google.pubsub_v1 import types as pubsub_types
from src.main import create_app
from src.extensions import db
from src.models.training_plan import TrainingPlan
from datetime import datetime
import json

class TestTrainingPlanUpdatesListener(unittest.TestCase):
    def setUp(self):
        self.app = create_app()
        self.app_context = self.app.app_context()
        self.app_context.push()
        db.create_all()
        self.add_sample_data()

    def tearDown(self):
        db.session.remove()
        db.drop_all()
        self.app_context.pop()

    def add_sample_data(self):
        trainingPlan = TrainingPlan(description="This plan focuses on building stamina through interval training.",exercises='Excersises test', duration="6 weeks", frequency="3 times a week", objectives="Increase height", profile_type="Beginner")
        db.session.add(trainingPlan)
        db.session.commit()

    @patch('google.cloud.pubsub_v1.SubscriberClient')
    def test_process_training_plan_created(self, mock_subscriber_client):
        mock_subscriber = mock_subscriber_client.return_value
        mock_subscriber.subscription_path.return_value = 'projects/project-id/subscriptions/subscription-id'

        # Prepare the fake message data
        training_plan_data = {
            "type": "TrainingPlanCreated",
            "data": {
                "objectives": "Increase height",
                "description": "This plan focuses on building stamina through interval training.",
                "exercises": "Excersises test",
                "frequency": "3 times a week",
                "duration": "6 weeks",
                "profile_type": "Beginner"
            }
        }
        message_data = json.dumps(training_plan_data).encode('utf-8')

        # Initialize the listener with a mocked subscriber client
        from src.queries.listen_training import EventUpdatesListener
        listener = EventUpdatesListener(self.app)
        message = MagicMock(data=message_data)
        message.ack = MagicMock()

        # Call the callback function directly with the mocked message
        listener.callback(message)

        # Assert the database was updated correctly
        new_training_plan = TrainingPlan.query.filter_by(objectives="Increase height").first()
        self.assertIsNotNone(new_training_plan)
        self.assertEqual(new_training_plan.duration, "6 weeks")

        # Assert the message was acknowledged
        message.ack.assert_called_once()

if __name__ == '__main__':
    unittest.main()
