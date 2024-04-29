import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.event import Event

class TestPublishEvent(TestCase):

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
            response = self.client.post('/events/1/publish')
            self.assertEqual(response.status_code, 200)
            self.assertIn("Event published successfully", response.json['message'])

    @patch('src.commands.publish_event.PublishEventCommandHandler.handle')
    def test_publish_non_existent_event(self, mock_handle):
        mock_handle.side_effect = ValueError("Event not found")
        with self.client:
            response = self.client.post('/events/999/publish')
            self.assertEqual(response.status_code, 400)
            self.assertIn("Event not found", response.json['error'])

    @patch('src.commands.publish_event.PublishEventCommandHandler.handle')
    def test_publish_event_in_wrong_state(self, mock_handle):
        mock_handle.side_effect = ValueError("Event is not in a state that can be published")
        with self.client:
            response = self.client.post('/events/1/publish')  # Assuming the initial state is not 'created'
            self.assertEqual(response.status_code, 400)
            self.assertIn("Event is not in a state that can be published", response.json['error'])

if __name__ == '__main__':
    unittest.main()
