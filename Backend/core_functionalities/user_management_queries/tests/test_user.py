import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.user import User

class TestUserQueryManagement(TestCase):
    def create_app(self):
        # Set up your Flask application with testing configuration
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        # Create the database tables if not already present
        db.create_all()
        # Optionally, populate some test users
        user1 = User(name="John", surname="Doe", id_type="ID", id_number="12345")
        user2 = User(name="Jane", surname="Smith", id_type="ID", id_number="67890")
        db.session.add(user1)
        db.session.add(user2)
        db.session.commit()

    def tearDown(self):
        # Drop all data after tests run
        db.session.remove()
        db.drop_all()

    def test_get_users(self):
        # Patch the query to return a controlled list
        with patch('src.models.user.User.query') as mock_query:
            mock_query.all.return_value = [
                User(id=1, name="John", surname="Doe"),
                User(id=2, name="Jane", surname="Smith")
            ]
            response = self.client.get('/users/get')
            self.assert200(response)
            self.assertEqual(len(response.json), 2)
            self.assertEqual(response.json[0]['name'], 'John')
            self.assertEqual(response.json[1]['name'], 'Jane')

    def test_get_user_by_id(self):
        # Patch the query to return a specific user
        with patch('src.models.user.User.query') as mock_query:
            mock_user = MagicMock()
            mock_user.to_dict.return_value = {'id': 1, 'name': 'John', 'surname': 'Doe'}
            mock_query.get.return_value = mock_user

            response = self.client.get('/users/1')
            self.assert200(response)
            self.assertEqual(response.json, {'id': 1, 'name': 'John', 'surname': 'Doe'})

if __name__ == '__main__':
    unittest.main()
