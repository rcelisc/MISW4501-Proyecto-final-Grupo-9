import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from sqlalchemy.exc import IntegrityError

from src.main import create_app
from src.extensions import db
from src.commands.create_event import CreateEventCommandHandler

class TestEventService(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        db.create_all()

    def tearDown(self):
        db.session.remove()
        db.drop_all()

    def test_create_event_success(self):
        # Mock the handle method to test endpoint integration
        with patch('src.api.event.CreateEventCommandHandler.handle', return_value=1) as mock_handle:
            event_data = {
                "name": "City Marathon 2024",
                "description": "Annual city marathon covering the downtown area.",
                "event_date": "2024-08-22T07:00:00",
                "duration": 5,
                "location": "Downtown City Park",
                "category": "Running",
                "fee": 50
            }
            response = self.client.post('/events', json=event_data)
            mock_handle.assert_called_once()
            self.assertStatus(response, 201)
            self.assertEqual(response.json['event_id'], 1)

    @patch('src.commands.create_event.CreateEventCommandHandler.handle')
    def test_create_event_missing_fields(self, mock_handle):
        # Test endpoint to ensure validation is happening
        mock_handle.side_effect = ValueError("Missing mandatory field(s): name, location")
        event_data = {
            "description": "Missing name and location",
            "event_date": "2024-08-22T07:00:00",
            "duration": 5,
            "category": "Running",
            "fee": 50
        }
        response = self.client.post('/events', json=event_data)
        self.assertStatus(response, 400)
        self.assertIn("Missing mandatory field(s): name, location", response.json['error'])

    @patch('src.extensions.db.session.commit')
    def test_create_event_database_error(self, mock_commit):
        mock_commit.side_effect = IntegrityError('mocking a db error', 'statement', 'params')
        handler = CreateEventCommandHandler()
        with self.assertRaises(ValueError) as context:
            handler.handle({
                "name": "Another Test Event",
                "description": "Test Description",
                "event_date": "2024-08-23T07:00:00",
                "duration": 4,
                "location": "Test Location",
                "category": "Test",
                "fee": 10
            })
        self.assertIn("Failed to create event due to a database error", str(context.exception))
