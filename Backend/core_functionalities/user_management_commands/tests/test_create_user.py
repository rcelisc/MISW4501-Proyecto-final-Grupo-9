import unittest
from unittest.mock import patch
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.user import Athlete, ComplementaryServicesProfessional, EventOrganizer
import json

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
    
    def test_handle_demographic_data(self):
        # Add a user to the database to update
        user_data = {
            "name": "John",
            "surname": "Doe",
            "id_type": "passport",
            "id_number": "1234567890",
            "city_of_living": "New York",
            "country_of_living": "USA",
            "weight": 70,
            "height": 175
        }
        user = Athlete(**user_data)
        db.session.add(user)
        db.session.commit()

        # Demographic data to be updated
        demographic_data = {
            "user_id": "1",
            "ethnicity": "Caucasian",
            "age": 30,
            "gender": "male",
            "heart_rate": 60,
            "vo2_max": 45,
            "blood_pressure": "120/80",
            "respiratory_rate": 16
        }

        # Patch the database commit and the pubsub client
        with patch('src.models.user.db.session.commit'), \
             patch('google.cloud.pubsub_v1.PublisherClient') as mock_pubsub:
            mock_publisher = mock_pubsub.return_value
            mock_publisher.topic_path.return_value = 'projects/dummy/topics/demographic-data-events'
            mock_publisher.publish.return_value = None

            # Simulate POST request to the demographic data update endpoint
            response = self.client.post(f'/users/{user.id}/demographic_data', json=demographic_data)
            self.assertStatus(response, 200)

            # Check the publish method was called correctly
            expected_event_data = json.dumps({
                "type": "DemographicDataUpdated",
                "data": demographic_data
            }).encode('utf-8')
            mock_publisher.publish.assert_called_with('projects/dummy/topics/demographic-data-events', expected_event_data)
        
    
    def test_handle_sports_habit_data(self):
        # Add a user to the database to update
        user_data = {
            "name": "John",
            "surname": "Doe",
            "id_type": "passport",
            "id_number": "1234567890",
            "city_of_living": "New York",
            "country_of_living": "USA",
            "weight": 70,
            "height": 175
        }
        user = Athlete(**user_data)
        db.session.add(user)
        db.session.commit()

        # Sports habit data to be updated
        sports_habit_data = {
            "user_id": "1",
            "training_frequency": 5,
            "sports_practiced": ["Running", "Cycling"],
            "average_session_duration": 90,
            "recovery_time": 48,
            "training_pace": "Moderate"
        }

        # Patch the database commit and the pubsub client
        with patch('src.models.user.db.session.commit'), \
             patch('google.cloud.pubsub_v1.PublisherClient') as mock_pubsub:
            mock_publisher = mock_pubsub.return_value
            mock_publisher.topic_path.return_value = 'projects/dummy/topics/sports-habit-events'
            mock_publisher.publish.return_value = None

            # Simulate POST request to the sports habit update endpoint
            response = self.client.post(f'/users/{user.id}/sports_habits', json=sports_habit_data)
            self.assertStatus(response, 200)

            # Check the publish method was called correctly
            expected_event_data = json.dumps({
                "type": "SportsHabitDataUpdated",
                "data": sports_habit_data
            }).encode('utf-8')
            mock_publisher.publish.assert_called_with('projects/dummy/topics/sports-habit-events', expected_event_data)

if __name__ == '__main__':
    unittest.main()
