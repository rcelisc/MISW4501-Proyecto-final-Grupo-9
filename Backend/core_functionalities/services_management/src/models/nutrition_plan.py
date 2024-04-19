from flask_sqlalchemy import SQLAlchemy
from ..extensions import db

class NutritionPlan(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    description = db.Column(db.String(255), nullable=False)
    user_id = db.Column(db.Integer, nullable=False)
    food_types = db.Column(db.Text, nullable=False)  # Detailed JSON field describing types of foods
    meal_frequency = db.Column(db.String(100), nullable=False)
    nutritional_objectives = db.Column(db.Text, nullable=False)  # JSON field with objectives

    def __repr__(self):
        return f'<NutritionPlan {self.id} for user {self.user_id}>'
