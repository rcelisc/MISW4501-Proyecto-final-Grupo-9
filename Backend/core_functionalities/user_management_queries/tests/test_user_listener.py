import unittest
from unittest.mock import patch, MagicMock
from google.cloud.pubsub_v1 import SubscriberClient
from google.cloud import pubsub_v1
from google.pubsub_v1 import types as pubsub_types
from src.main import create_app
from src.extensions import db
from src.models.user import User, Athlete, ComplementaryServicesProfessional, EventOrganizer
import json
from datetime import datetime

class TestUserUpdatesListener(unittest.TestCase):
    def setUp(self):
        self.app = create_app()
        self.app_context = self.app.app_context()
        self.app_context.push()
        db.create_all()
        self.add_sample_user()

    def tearDown(self):
        db.session.remove()
        db.drop_all()
        self.app_context.pop()

    def add_sample_user(self):
        user = Athlete(type= "athlete", name= "John", surname= "Doe", id_type= "ID card of citizenship",id_number= "1234567890", city_of_living= "New York", country_of_living= "USA", age= 30, gender= "Male", weight= 70, height= 175, city_of_birth= "New York", country_of_birth= "USA", sports= "cycling, running", profile_type= "Beginner")
        db.session.add(user)
        db.session.commit()

    @patch('google.cloud.pubsub_v1.SubscriberClient')
    def test_process_user_created(self, mock_subscriber_client):
        mock_subscriber = mock_subscriber_client.return_value
        mock_subscriber.subscription_path.return_value = 'projects/project-id/subscriptions/subscription-id'

        # Prepare the fake user creation message
        user_data = {
            "type": "UserCreated",
            "data": {
                "name": "Alice Smith",
                "surname": "Smith",
                "type": "athlete",
                "id_type": "passport",
                "id_number": "987654321",
                "age": 28,
                "gender": "female",
                "sports": ["tennis"]
            }
        }
        message_data = json.dumps(user_data).encode('utf-8')

        # Initialize the listener with a mocked subscriber client
        from src.queries.listen_user import EventUpdatesListener
        listener = EventUpdatesListener(self.app)
        message = MagicMock(data=message_data)
        message.ack = MagicMock()

        # Call the callback function directly with the mocked message
        listener.callback(message)

        # Assert the database was updated correctly
        new_user = Athlete.query.filter_by(id_number="987654321").first()
        self.assertIsNotNone(new_user)
        self.assertEqual(new_user.name, "Alice Smith")

        # Assert the message was acknowledged
        message.ack.assert_called_once()
    
    @patch('google.cloud.pubsub_v1.SubscriberClient')
    def test_process_demographic_data_updated(self, mock_subscriber_client):
        # Mock the subscriber client
        mock_subscriber = mock_subscriber_client.return_value
        mock_subscriber.subscription_path.return_value = 'projects/project-id/subscriptions/demographic-data-events-sub'

        # Prepare the fake demographic update message
        demographic_data = {
            "type": "DemographicDataUpdated",
            "data": {
                "user_id": 1,
                "ethnicity": "Hispanic",
                "heart_rate": 72,
                "vo2_max": 50,
                "blood_pressure": "120/80",
                "respiratory_rate": 15
            }
        }
        message_data = json.dumps(demographic_data).encode('utf-8')

        # Initialize the listener with a mocked subscriber client
        from src.queries.listen_demographic_data import EventUpdatesListener
        listener = EventUpdatesListener(self.app)
        message = MagicMock(data=message_data)
        message.ack = MagicMock()

        # Call the callback function directly with the mocked message
        listener.callback(message)

        # Fetch the updated user from the database
        updated_user = Athlete.query.get(1)
        self.assertIsNotNone(updated_user)
        self.assertEqual(updated_user.ethnicity, "Hispanic")
        self.assertEqual(updated_user.heart_rate, 72)
        self.assertEqual(updated_user.vo2_max, 50)
        self.assertEqual(updated_user.blood_pressure, "120/80")
        self.assertEqual(updated_user.respiratory_rate, 15)

        # Assert the message was acknowledged
        message.ack.assert_called_once()

    @patch('google.cloud.pubsub_v1.SubscriberClient')
    def test_process_sports_habit_updated(self, mock_subscriber_client):
        # Mock the subscriber client
        mock_subscriber = mock_subscriber_client.return_value
        mock_subscriber.subscription_path.return_value = 'projects/project-id/subscriptions/sports-habit-events-sub'

        # Prepare the fake sports habit update message
        sports_habit_data = {
            "type": "SportsHabitDataUpdated",
            "data": {
                "user_id": 1,
                "training_frequency": "4",
                "sports_practiced": "Running",
                "average_session_duration": 60,
                "recovery_time": 48,
                "training_pace": "Moderate"
            }
        }
        message_data = json.dumps(sports_habit_data).encode('utf-8')

        # Initialize the listener with a mocked subscriber client
        from src.queries.listen_sports_habits import EventUpdatesListener
        listener = EventUpdatesListener(self.app)
        message = MagicMock(data=message_data)
        message.ack = MagicMock()

        # Call the callback function directly with the mocked message
        listener.callback(message)

        # Fetch the updated user from the database
        updated_user = Athlete.query.get(1)
        self.assertIsNotNone(updated_user)
        self.assertEqual(updated_user.training_frequency, "4")
        self.assertEqual(updated_user.sports_practiced, "Running"),
        self.assertEqual(updated_user.average_session_duration, 60)
        self.assertEqual(updated_user.recovery_time, 48)
        self.assertEqual(updated_user.training_pace, "Moderate")

        # Assert the message was acknowledged
        message.ack.assert_called_once()

if __name__ == '__main__':
    unittest.main()
