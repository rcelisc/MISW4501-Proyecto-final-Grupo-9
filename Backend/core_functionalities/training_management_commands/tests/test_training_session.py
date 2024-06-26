import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.training_session import TrainingSession
from datetime import datetime, timedelta
import uuid
import jwt
class TestTrainingSessionManagement(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestTrainingSessionManagement, self).setUp()
        db.create_all()
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

    def test_start_training_session_success(self):
        # Mocking the handler's start method to return a session ID
        with patch('src.api.training_session.StartTrainingSessionCommandHandler.start', return_value=1):
            test_data = {"user_id": 1, "training_type": "Cardio"}
            headers = {'Authorization': 'Bearer ' + self.valid_token}
            response = self.client.post('/start-training', json=test_data, headers=headers)
            self.assertStatus(response, 201)
            self.assertEqual(response.json, {"session_id": 1})

    def test_stop_training_session_success(self):
            # Creating a session with a UUID instead of an integer
            session_uuid = uuid.uuid4()
            session = TrainingSession(id=session_uuid, user_id=1, start_time=datetime.now(), training_type="Cardio")
            db.session.add(session)
            db.session.commit()

            # Mocking the handler's stop method to return the UUID
            with patch('src.api.training_session.StopTrainingSessionCommandHandler.stop', return_value=session_uuid):
                test_data = {"session_id": session_uuid}
                headers = {'Authorization': 'Bearer ' + self.valid_token}
                response = self.client.post('/stop-training', json=test_data, headers=headers)
                self.assertStatus(response, 200)
                self.assertEqual(response.json, {"message": "Training session stopped successfully", "session_id": str(session_uuid)})


    def test_receive_session_data_success(self):
            # Adding a session for which to receive data
            session_uuid = uuid.uuid4()
            session = TrainingSession(id=session_uuid, user_id=1, start_time=datetime.now(), training_type="Cardio")
            db.session.add(session)
            db.session.commit()

            # Mocking data reception
            test_data = {"session_id": session_uuid, "power_output": 300, "max_heart_rate": 180, "resting_heart_rate": 60}
            headers = {'Authorization': 'Bearer ' + self.valid_token}
            response = self.client.post('/receive_session-data', json=test_data, headers=headers)
            self.assertStatus(response, 200)
            self.assertEqual(response.json, {"message": "Session data updated successfully"})
