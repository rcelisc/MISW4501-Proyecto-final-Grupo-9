import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.event import Event
from datetime import datetime, timedelta
import jwt

class TestPublishEvent(TestCase):

    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestPublishEvent, self).setUp()
        db.create_all()
        self.create_initial_events()
        self.valid_token = self.generate_fake_token()

    def tearDown(self):
        db.session.remove()
        db.drop_all()
    
    def generate_fake_token(self):
        payload = {
            'user_id': 1,
            'role': 'event_organizer',
            'exp': datetime.utcnow() + timedelta(days=1)
        }
        return jwt.encode(payload, 'login_key', algorithm='HS256')

    def create_initial_events(self):
        event = Event(
            id=1,
            name="Initial Event",
            description="Description of initial event.",
            event_date="2025-01-01T10:00:00",
            duration=2,
            location="Initial Location",
            category="Initial Category",
            fee=20.0,
            attendees={},
            status='created'  # Ensure the event is in a creatable state
        )
        db.session.add(event)
        db.session.commit()

    @patch('src.commands.publish_event.PublishEventCommandHandler.handle')
    def test_publish_event_successfully(self, mock_handle):
        with self.client:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.post('/events/1/publish', headers=headers)
            self.assertEqual(response.status_code, 200)
            self.assertIn("Event published successfully", response.json['message'])

    @patch('src.commands.publish_event.PublishEventCommandHandler.handle')
    def test_publish_non_existent_event(self, mock_handle):
        mock_handle.side_effect = ValueError("Event not found")
        with self.client:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.post('/events/999/publish', headers=headers)
            self.assertEqual(response.status_code, 400)
            self.assertIn("Event not found", response.json['error'])

    @patch('src.commands.publish_event.PublishEventCommandHandler.handle')
    def test_publish_event_in_wrong_state(self, mock_handle):
        mock_handle.side_effect = ValueError("Event is not in a state that can be published")
        with self.client:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.post('/events/1/publish', headers=headers)  # Assuming the initial state is not 'created'
            self.assertEqual(response.status_code, 400)
            self.assertIn("Event is not in a state that can be published", response.json['error'])

if __name__ == '__main__':
    unittest.main()
