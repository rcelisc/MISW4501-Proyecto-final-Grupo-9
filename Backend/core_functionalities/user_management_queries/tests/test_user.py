import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.user import User
from datetime import datetime, timedelta
import jwt

class TestUserQueryManagement(TestCase):
    def create_app(self):
        # Set up your Flask application with testing configuration
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestUserQueryManagement, self).setUp()
        # Create the database tables if not already present
        db.create_all()
        # Optionally, populate some test users
        self.user1 = User(id=1, name="John", surname="Doe", id_type="ID", id_number="12345", type="athlete")
        self.user2 = User(id=2, name="Jane", surname="Smith", id_type="ID", id_number="67890", type="event_organizer")
        db.session.add(self.user1)
        db.session.add(self.user2)
        db.session.commit()
        self.valid_token_1 = self.generate_fake_token(1, 'athlete')
        self.valid_token_2 = self.generate_fake_token(2, 'event_organizer')

    def tearDown(self):
        # Drop all data after tests run
        db.session.remove()
        db.drop_all()
    
    def generate_fake_token(self,user_id, role):
        payload = {
            'user_id': user_id,
            'role': role,
            'exp': datetime.utcnow() + timedelta(days=1)
        }
        return jwt.encode(payload, 'login_key', algorithm='HS256')

    def test_get_users(self):
        headers = {'Authorization': 'Bearer ' + self.valid_token_2}
        response = self.client.get('/users/get', headers=headers)
        self.assert200(response)
        print("RESPONSE",response.json)
        self.assertEqual(len(response.json), 1)
        self.assertEqual(response.json[0]['name'], 'John')


    def test_get_user_by_id(self):
        headers = {'Authorization': 'Bearer ' + self.valid_token_1}
        response = self.client.get(f'/users/{self.user1.id}', headers=headers)
        expected_output = self.user1.to_dict()
        self.assert200(response)
        self.assertEqual(response.json, expected_output)

if __name__ == '__main__':
    unittest.main()
