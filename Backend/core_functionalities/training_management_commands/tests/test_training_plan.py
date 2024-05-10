import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.training_plan import TrainingPlan
from datetime import datetime, timedelta
import jwt

class TestTrainingPlanManagement(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestTrainingPlanManagement, self).setUp()
        db.create_all()
        self.valid_token = self.generate_fake_token('complementary_services_professional')

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

    def test_create_training_plan_success(self):
        # Mock the Google Pub/Sub client and the handler method
        with patch('src.api.training_plan.CreateTrainingPlanCommandHandler.handle', return_value=1) as mock_handle:
            test_data = {
                "description": "Strength Training",
                "exercises": ["Bench Press", "Deadlift"],
                "duration": 60,
                "frequency": 3,
                "objectives": ["Build muscle", "Increase strength"],
                "profile_type": "Athlete"
            }
            headers = {'Authorization': 'Bearer ' + self.valid_token}
            response = self.client.post('/training-plan', json=test_data, headers=headers)
            self.assertStatus(response, 201)
            self.assertEqual(response.json, {"id": 1})
            mock_handle.assert_called_once_with(test_data)
