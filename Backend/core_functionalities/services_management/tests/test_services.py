import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from sqlalchemy.exc import IntegrityError

from src.main import create_app
from src.extensions import db
from src.models.services import Service

class TestServiceManagement(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        db.create_all()
        # Populate the test database with data
        service1 = Service(id=1, name='Service A', description='Test A', available=True, rate=100)
        service2 = Service(id=2, name='Service B', description='Test B', available=True, rate=150)
        db.session.add(service1)
        db.session.add(service2)
        db.session.commit()

    def tearDown(self):
        db.session.remove()
        db.drop_all()

    def test_create_service_success(self):
        test_data = {"name": "Service A", "description": "Test service", "available": True, "rate": 100.0, "status": "created"}
        with patch('src.api.services.create_service', return_value=1) as mock_create_service:
            response = self.client.post('/services', json=test_data)
            mock_create_service.assert_called_once_with(test_data)
            self.assertStatus(response, 201)
            self.assertEqual(response.json, {"message": "Service created successfully", "service_id": 1})

    def test_get_services_with_events(self):
        # Mock the external API call for events
        with patch('requests.get') as mock_get:
            mock_response = MagicMock(status_code=200)
            mock_response.json.return_value = {'events': [{'id': 1, 'name': 'Event 1'}]}
            mock_get.return_value = mock_response

            response = self.client.get('/services')
            self.assert200(response)
            expected_services = [
                {'id': 1, 'name': 'Service A', 'description': 'Test A', 'available': True, 'rate': 100},
                {'id': 2, 'name': 'Service B', 'description': 'Test B', 'available': True, 'rate': 150}
            ]
            expected_events = {'events': [{'id': 1, 'name': 'Event 1'}]}

            # Ensure both services and events are as expected
            self.assertEqual(response.json['services'], expected_services)
            self.assertEqual(response.json['events'], expected_events)

    def test_publish_service_success(self):
        with patch('src.api.services.publish_service') as mock_publish_service:
            response = self.client.post('/services/1/publish')
            mock_publish_service.assert_called_once_with(1)
            self.assert200(response)
            self.assertEqual(response.json, {"message": "Service published successfully"})


    def test_publish_service_not_found(self):
        with patch('src.commands.publish_service.publish_service', side_effect=ValueError("Service not found")):
            response = self.client.post('/services/999/publish')
            self.assert400(response)
            self.assertIn("Service not found", response.json['error'])
