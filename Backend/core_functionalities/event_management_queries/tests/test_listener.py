import unittest
from unittest.mock import patch, MagicMock
from google.cloud.pubsub_v1 import SubscriberClient
from google.cloud import pubsub_v1
from google.pubsub_v1 import types as pubsub_types
from src.main import create_app
from src.extensions import db
from src.models.event import Event
from datetime import datetime
import json

class TestEventUpdatesListener(unittest.TestCase):
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
        event = Event(name="Sample Event",description="A event.",event_date=datetime.utcnow(), duration=2, location="Park", category="Public", fee=0)
        db.session.add(event)
        db.session.commit()

    @patch('google.cloud.pubsub_v1.SubscriberClient')
    def test_process_event_created(self, mock_subscriber_client):
        mock_subscriber = mock_subscriber_client.return_value
        mock_subscriber.subscription_path.return_value = 'projects/project-id/subscriptions/subscription-id'

        # Prepare the fake message data
        event_data = {
            "type": "EventCreated",
            "data": {
                "name": "New Event",
                "description": "A new event.",
                "event_date": "2024-05-20T12:00:00",
                "duration": 3,
                "location": "New Park",
                "category": "Outdoor",
                "fee": 10
            }
        }
        message_data = json.dumps(event_data).encode('utf-8')

        # Initialize the listener with a mocked subscriber client
        from src.queries.events_update_listener import EventUpdatesListener
        listener = EventUpdatesListener(self.app)
        message = MagicMock(data=message_data)
        message.ack = MagicMock()

        # Call the callback function directly with the mocked message
        listener.callback(message)

        # Assert the database was updated correctly
        new_event = Event.query.filter_by(name="New Event").first()
        self.assertIsNotNone(new_event)
        self.assertEqual(new_event.location, "New Park")

        # Assert the message was acknowledged
        message.ack.assert_called_once()

    @patch('google.cloud.pubsub_v1.SubscriberClient')
    def test_process_user_added(self, mock_subscriber_client):
        mock_subscriber = mock_subscriber_client.return_value
        mock_subscriber.subscription_path.return_value = 'projects/project-id/subscriptions/subscription-id'

        # Prepare the fake message data
        event_data = {
            "type": "UserAddedToEvent",
            "event_id": 1,
            "user_id": 1
        }
        message_data = json.dumps(event_data).encode('utf-8')

        # Initialize the listener with a mocked subscriber client
        from src.queries.events_update_listener import EventUpdatesListener
        listener = EventUpdatesListener(self.app)
        message = MagicMock(data=message_data)
        message.ack = MagicMock()

        # Call the callback function directly with the mocked message
        listener.callback(message)

        # Assert the database was updated correctly
        event = Event.query.get(1)
        self.assertIsNotNone(event)
        self.assertIn(1, event.attendees["user_ids"])

        # Assert the message was acknowledged
        message.ack.assert_called_once()

if __name__ == '__main__':
    unittest.main()
