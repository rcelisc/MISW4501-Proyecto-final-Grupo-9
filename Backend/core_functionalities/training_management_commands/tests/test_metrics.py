import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.training_session import TrainingSession
import uuid
from datetime import datetime, timedelta
import jwt

class TestTrainingMetrics(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestTrainingMetrics, self).setUp()
        db.create_all()
        self.session_id = uuid.uuid4()  # Generate a unique session ID
        start_time = datetime.now() - timedelta(hours=1)
        end_time = datetime.now()
        session = TrainingSession(id=self.session_id, user_id=1, start_time=start_time, end_time=end_time,
                                  training_type='default', duration=3600, power_output=300,
                                  max_heart_rate=180, resting_heart_rate=60)
        db.session.add(session)
        db.session.commit()
        self.valid_token = self.generate_fake_token('athlete')

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

    def test_calculate_ftp_vo2max_success(self):
        # Mock the pubsub publisher
        with patch('google.cloud.pubsub_v1.PublisherClient') as mock_publisher:
            mock_publisher_instance = MagicMock()
            mock_publisher.return_value = mock_publisher_instance
            headers = {'Authorization': 'Bearer ' + self.valid_token}
            response = self.client.post('/calculate-ftp-vo2max', json={'session_id': str(self.session_id)}, headers=headers)
            self.assert200(response)
            expected_results = {"FTP": 7.916666666666666, "VO2max": 45.0}
            self.assertEqual(response.json, expected_results)
            mock_publisher_instance.publish.assert_called_once()

    def test_calculate_ftp_vo2max_session_not_found(self):
        non_existent_id = uuid.uuid4()  # Generate a non-existent session UUID
        with patch('google.cloud.pubsub_v1.PublisherClient'):
            headers = {'Authorization': 'Bearer ' + self.valid_token}
            response = self.client.post('/calculate-ftp-vo2max', json={'session_id': str(non_existent_id)}, headers=headers)
            self.assert200(response)
            self.assertEqual(response.json, {"error": "Session not found"})