import unittest
from unittest.mock import patch
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.user import Athlete, ComplementaryServicesProfessional, EventOrganizer

class TestUserManagement(TestCase):
    def create_app(self):
        # Set up your Flask application with testing configuration
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        # Create the database tables if not already present
        db.create_all()

    def tearDown(self):
        # Drop all data after tests run
        db.session.remove()
        db.drop_all()

    def test_create_user_athlete(self):
        # Data for creating a new user
        user_data = {
            "name": "John",
            "surname": "Doe",
            "id_type": "passport",
            "id_number": "1234567890",
            "city_of_living": "New York",
            "country_of_living": "USA",
            "age": 30,
            "gender": "male",
            "weight": 70,
            "height": 175
        }
        # Patch the database session commit and the pubsub client
        with patch('src.commands.create_user.db.session.commit'), \
             patch('src.commands.create_user.pubsub_v1.PublisherClient') as mock_pubsub:
            # Mock the publish method
            mock_publisher = mock_pubsub.return_value
            mock_publisher.topic_path.return_value = 'projects/dummy/topics/user-events'
            mock_publisher.publish.return_value = None

            response = self.client.post('/users', json=user_data)
            self.assertStatus(response, 201)
            self.assertIn('id', response.json)

    def test_create_user_default_type(self):
        # Data missing 'type', should default to 'athlete'
        user_data = {
            "name": "Jane",
            "surname": "Smith",
            "id_type": "passport",
            "id_number": "9876543210"
        }
        with patch('src.commands.create_user.db.session.commit'), \
             patch('src.commands.create_user.pubsub_v1.PublisherClient'):
            response = self.client.post('/users', json=user_data)
            self.assertStatus(response, 201)
            self.assertIn('id', response.json)

if __name__ == '__main__':
    unittest.main()
