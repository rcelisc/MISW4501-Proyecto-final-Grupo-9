import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.event import Event
from datetime import datetime, timedelta
import jwt
class TestUpdateEvent(TestCase):

    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestUpdateEvent, self).setUp()
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
            user_id=1  # Set the user_id here

        )
        db.session.add(event)
        db.session.commit()

    @patch('src.commands.update_event.UpdateEventCommandHandler.handle')
    def test_update_event_successfully(self, mock_handle):
        mock_handle.return_value = 1
        with self.client:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.put('/events/1', json={
                "name": "Updated Event",
                "description": "Updated description.",
                "event_date": "2025-01-02T10:00:00",
                "duration": 3,
                "location": "Updated Location",
                "category": "Updated Category",
                "fee": 25.0
            }, headers=headers)
            self.assertEqual(response.status_code, 200)
            self.assertIn("Event updated successfully", response.json['message'])

    @patch('src.commands.update_event.UpdateEventCommandHandler.handle')
    def test_update_non_existent_event(self, mock_handle):
        mock_handle.side_effect = ValueError("Event not found")
        with self.client:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.put('/events/999', json={
                "name": "Non-existent Event",
            }, headers=headers)
            self.assertEqual(response.status_code, 400)
            self.assertIn("Event not found", response.json['error'])

    @patch('src.commands.update_event.UpdateEventCommandHandler.handle')
    def test_update_event_with_missing_fields(self, mock_handle):
        mock_handle.side_effect = ValueError("Missing mandatory field(s): name, description")
        with self.client:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.put('/events/1', json={}, headers=headers)
            self.assertEqual(response.status_code, 400)
            self.assertIn("Missing mandatory field(s): name, description", response.json['error'])

if __name__ == '__main__':
    unittest.main()
