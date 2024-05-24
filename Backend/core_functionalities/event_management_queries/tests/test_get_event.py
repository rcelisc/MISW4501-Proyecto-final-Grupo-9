import unittest
from unittest.mock import patch
from flask_testing import TestCase
from datetime import datetime

from src.main import create_app
from src.extensions import db
from src.models.event import Event
from datetime import datetime, timedelta
import jwt
class TestEventQueries(TestCase):

    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestEventQueries, self).setUp()
        db.create_all()
        self.create_initial_events()
        self.valid_token = self.generate_fake_token('event_organizer')

    def tearDown(self):
        db.session.remove()
        db.drop_all()
    
    def generate_fake_token(self,role):
        payload = {
            'user_id': 1,
            'role': role,
            'exp': datetime.utcnow() + timedelta(days=1)
        }
        return jwt.encode(payload, 'login_key', algorithm='HS256')

    def create_initial_events(self):
        event1 = Event(
            id=1,
            name="Public Event",
            description="A public event.",
            event_date=datetime.utcnow(),
            duration=2,
            location="City Center",
            category="Public",
            fee=0.0,
            status='published',
            additional_info={},
            user_id=1
        )
        event2 = Event(
            id=2,
            name="Private Event",
            description="A private event.",
            event_date=datetime.utcnow(),
            duration=3,
            location="Private Venue",
            category="Private",
            fee=20.0,
            status='created',
            additional_info={},
            user_id=1
        )
        db.session.add(event1)
        db.session.add(event2)
        db.session.commit()

    def test_get_events(self):
        self.valid_token = self.generate_fake_token('event_organizer')
        headers = {'Authorization': f'Bearer {self.valid_token}'}
        response = self.client.get('/events/get', headers=headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.json), 2)  # Expecting two events

    def test_get_event(self):
        self.valid_token = self.generate_fake_token('event_organizer')
        headers = {'Authorization': f'Bearer {self.valid_token}'}
        response = self.client.get('/events/1', headers=headers)
        self.assertEqual(response.status_code, 200)
        self.assertIn('Public Event', response.json['name'])

    def test_get_nonexistent_event(self):
        self.valid_token = self.generate_fake_token('event_organizer')
        headers = {'Authorization': f'Bearer {self.valid_token}'}
        response = self.client.get('/events/999', headers=headers)
        self.assertEqual(response.status_code, 404)
        self.assertIn('Event not found', response.json['error'])

    def test_get_published_events(self):
        self.valid_token = self.generate_fake_token('event_organizer')
        headers = {'Authorization': f'Bearer {self.valid_token}'}
        response = self.client.get('/events/published', headers=headers)
        self.assertEqual(response.status_code, 200)
        self.assertEqual(len(response.json), 1)  # Only one published event
        self.assertIn('Public Event', response.json[0]['name'])

    def test_get_user_calendar(self):
        with patch('src.queries.get_user_calendar.GetUserCalendarQueryHandler.execute') as mock_execute:
            mock_execute.return_value = [{'event_id': 1, 'name': 'User Event'}]
            self.valid_token = self.generate_fake_token('athlete')
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.get('/user/123/calendar', headers=headers)
            self.assertEqual(response.status_code, 200)
            self.assertEqual(response.json[0]['name'], 'User Event')

if __name__ == '__main__':
    unittest.main()
