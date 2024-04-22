import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.nutrition_plan import NutritionPlan

class TestNutritionPlanManagement(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        db.create_all()
        # Populate with sample nutrition plan
        plan1 = NutritionPlan(id=1, description="Low carb diet", user_id=1, food_types=["Vegetables", "Fruits"], meal_frequency=3, nutritional_objectives="Weight loss")
        db.session.add(plan1)
        db.session.commit()

    def tearDown(self):
        db.session.remove()
        db.drop_all()

    def test_get_nutrition_plans(self):
        # Mocking the query handler for fetching plans
        with patch('src.api.nutrition_plan.GetNutritionPlansQueryHandler.handle', return_value=[
            {'id': 1, 'description': "Low carb diet", 'user_id': 1, 'food_types': ["Vegetables", "Fruits"], 'meal_frequency': 3, 'nutritional_objectives': "Weight loss"}
        ]) as mock_handle:
            response = self.client.get('/nutrition-plans')
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
            response = self.client.post('/nutrition-plans', json=test_data)
            self.assertStatus(response, 201)
            self.assertEqual(response.json, {"message": "Nutrition plan created successfully", "nutrition_plan_id": 1})
            mock_handle.assert_called_once_with(test_data)
