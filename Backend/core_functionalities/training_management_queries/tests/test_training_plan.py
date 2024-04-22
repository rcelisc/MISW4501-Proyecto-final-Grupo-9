import unittest
from unittest.mock import patch, MagicMock
from flask_testing import TestCase
from src.main import create_app
from src.extensions import db
from src.models.training_plan import TrainingPlan
from src.queries.get_training_plan import GetTrainingPlanQueryHandler, GetTrainingPlansByProfileQueryHandler

class TestTrainingPlanQueries(TestCase):
    def create_app(self):
        app = create_app()
        app.config.from_object('src.config.TestingConfig')
        return app

    def setUp(self):
        db.create_all()
        # Pre-populate the database with mock data
        plan1 = TrainingPlan(id=1, description="Plan A", objectives="Increase Stamina", exercises= "breast, legs", frequency= "3 times a week", duration= "6 weeks", profile_type="Beginner")
        plan2 = TrainingPlan(id=2, description="Plan B", objectives="Increase Stamina", exercises= "breast, arms", frequency= "5 times a week", duration= "6 weeks", profile_type="Intermediate")
        db.session.add_all([plan1, plan2])
        db.session.commit()

    def tearDown(self):
        db.session.remove()
        db.drop_all()

    def test_get_training_plan(self):
        # Testing the GET endpoint for a single training plan
        with patch.object(GetTrainingPlanQueryHandler, 'handle', return_value={'id': 1, 'description': 'Plan A', 'profile_type': 'Beginner'}) as mock_handle:
            response = self.client.get('/training-plan/1')
            mock_handle.assert_called_once_with(1)
            self.assertStatus(response, 200)
            self.assertEqual(response.json, {
                'id': 1,
                'description': 'Plan A',
                'profile_type': 'Beginner'
            })

    def test_get_training_plans_by_profile(self):
        # Testing the GET endpoint for training plans filtered by profile type
        expected_plans = [
            {'id': 1, 'description': 'Plan A', 'profile_type': 'Beginner'},
            {'id': 2, 'description': 'Plan B', 'profile_type': 'Intermediate'}
        ]
        with patch.object(GetTrainingPlansByProfileQueryHandler, 'handle', return_value=expected_plans) as mock_handle:
            response = self.client.get('/training-plans/Beginner')
            mock_handle.assert_called_once_with('Beginner')
            self.assertStatus(response, 200)
            self.assertEqual(response.json, expected_plans)

# To run the tests
if __name__ == '__main__':
    unittest.main()
