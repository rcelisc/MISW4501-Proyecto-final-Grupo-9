import unittest
from unittest.mock import patch
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.training_session import TrainingSession
from datetime import datetime, timedelta
import uuid
import jwt

class TestTrainingSessionManagement(TestCase):
    def create_app(self):
        # Initialize the Flask application with test configuration
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestTrainingSessionManagement, self).setUp()
        # Create all database tables and seed test data
        db.create_all()
        session1 = TrainingSession(id='123e4567-e89b-12d3-a456-426614174000', session_id='123e4567-e89b-12d3-a456-426614174000', user_id=1, end_time=datetime.now() + timedelta(hours=1), training_type='cycling', duration=60, notes='Good session')
        session2 = TrainingSession(id='123e4567-e89b-12d3-a456-426614174001', session_id='123e4567-e89b-12d3-a456-426614174001', user_id=2, end_time=datetime.now() + timedelta(hours=2), training_type='running', duration=120, notes='Very intense')
        db.session.add(session1)
        db.session.add(session2)
        db.session.commit()
        self.valid_token = self.generate_fake_token('complementary_services_professional')

    def tearDown(self):
        # Drop all tables and clean up the test database
        db.session.remove()
        db.drop_all()

    def generate_fake_token(self, role):
        # Generate a fake JWT token for testing
        payload = {
            'user_id': 1,
            'role': role,
            'exp': datetime.utcnow() + timedelta(days=1)
        }
        return jwt.encode(payload, 'login_key', algorithm='HS256')

    def test_get_all_training_sessions(self):
        # Test retrieval of all training sessions
        headers = {'Authorization': 'Bearer ' + self.valid_token}
        response = self.client.get('/training-sessions', headers=headers)
        self.assert200(response)
        self.assertEqual(len(response.json), 2)  # Check if two sessions are returned
        self.assertTrue(any(session['id'] == '123e4567-e89b-12d3-a456-426614174000' for session in response.json))
        self.assertTrue(any(session['id'] == '123e4567-e89b-12d3-a456-426614174001' for session in response.json))

    def test_get_training_session_by_id(self):
        # Test retrieval of a specific training session by UUID
        headers = {'Authorization': 'Bearer ' + self.valid_token}
        response = self.client.get('/training-sessions/123e4567-e89b-12d3-a456-426614174000', headers=headers)
        self.assert200(response)
        # Check if the response has valid JSON data
        self.assertIsNotNone(response.json, "The response JSON should not be None")
        # Further checks if response JSON is valid
        self.assertIn('id', response.json, "The response JSON should contain an 'id' key")
        self.assertIn('training_type', response.json, "The response JSON should contain a 'training_type' key")
        # Assertions to test the actual values
        self.assertEqual(response.json['id'], '123e4567-e89b-12d3-a456-426614174000')
        self.assertEqual(response.json['training_type'], 'cycling')

# Run the test suite
if __name__ == '__main__':
    unittest.main()
