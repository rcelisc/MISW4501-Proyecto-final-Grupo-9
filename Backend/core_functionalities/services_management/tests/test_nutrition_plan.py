import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.nutrition_plan import NutritionPlan
from datetime import datetime, timedelta
import jwt
class TestNutritionPlanManagement(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        super(TestNutritionPlanManagement, self).setUp()
        db.create_all()
        # Populate with sample nutrition plan
        plan1 = NutritionPlan(id=1, description="Low carb diet", user_id=1, food_types=["Vegetables", "Fruits"], meal_frequency=3, nutritional_objectives="Weight loss")
        db.session.add(plan1)
        db.session.commit()
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

    def test_get_nutrition_plans(self):
        # Mocking the query handler for fetching plans
        with patch('src.api.nutrition_plan.GetNutritionPlansQueryHandler.handle', return_value=[
            {'id': 1, 'description': "Low carb diet", 'user_id': 1, 'food_types': ["Vegetables", "Fruits"], 'meal_frequency': 3, 'nutritional_objectives': "Weight loss"}
        ]) as mock_handle:
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.get('/nutrition-plans', headers=headers)
            self.assert200(response)
            expected_data = [
                {'id': 1, 'description': "Low carb diet", 'user_id': 1, 'food_types': ["Vegetables", "Fruits"], 'meal_frequency': 3, 'nutritional_objectives': "Weight loss"}
            ]
            self.assertEqual(response.json, expected_data)
            mock_handle.assert_called_once()

    def test_create_nutrition_plan_success(self):
        # Mock the Google Pub/Sub client and the handler method
        with patch('src.api.nutrition_plan.CreateNutritionPlanCommandHandler.handle', return_value=1) as mock_handle:
            test_data = {"description": "Keto diet", "user_id": 2, "food_types": ["Meat", "Cheese"], "meal_frequency": 2, "nutritional_objectives": "Muscle gain"}
            headers = {'Authorization': f'Bearer {self.valid_token}'}
            response = self.client.post('/nutrition-plans', json=test_data, headers=headers)
            self.assertStatus(response, 201)
            self.assertEqual(response.json, {"message": "Nutrition plan created successfully", "nutrition_plan_id": 1})
            mock_handle.assert_called_once_with(test_data)
