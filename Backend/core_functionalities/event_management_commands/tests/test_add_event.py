import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.event import Event

class TestAddUserToEvent(TestCase):

    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        db.create_all()
        self.create_initial_events()

    def tearDown(self):
        db.session.remove()
        db.drop_all()

    def create_initial_events(self):
        # Create a default event to be used in tests
        event = Event(
            id=1,
            name="Test Event",
            description="A sample event for testing.",
            event_date="2025-01-01T00:00:00",
            duration=1,
            location="Test Location",
            category="Test Category",
            fee=0.0,
            attendees={'user_ids': []}
        )
        db.session.add(event)
        db.session.commit()

    @patch('src.commands.add_user_to_event.pubsub_v1.PublisherClient')
    def test_add_user_successfully(self, mock_pubsub_client):
        mock_publisher = MagicMock()
        mock_pubsub_client.return_value = mock_publisher
        
        with self.client:
            response = self.client.post('/events/1/add', json={'user_id': 123})
            self.assertEqual(response.status_code, 200)
            self.assertIn("User added successfully", response.json['message'])

    @patch('src.commands.add_user_to_event.pubsub_v1.PublisherClient')
    def test_add_user_to_non_existent_event(self, mock_pubsub_client):
        with self.client:
            response = self.client.post('/events/999/add', json={'user_id': 123})
            self.assertEqual(response.status_code, 404)
            self.assertIn("Event not found", response.json['error'])

    @patch('src.commands.add_user_to_event.pubsub_v1.PublisherClient')
    def test_add_existing_user_to_event(self, mock_pubsub_client):
        # Manually adding a user to the initial event setup
        event = Event.query.get(1)
        event.attendees['user_ids'].append(123)
        db.session.commit()

        with self.client:
            response = self.client.post('/events/1/add', json={'user_id': 123})
            self.assertEqual(response.status_code, 200)
            self.assertIn("User added successfully", response.json['message'])

if __name__ == '__main__':
    unittest.main()
