from flask_sqlalchemy import SQLAlchemy
from ..extensions import db

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    surname = db.Column(db.String(100))
    id_type = db.Column(db.String(50))
    id_number = db.Column(db.String(50), nullable=False)
    city_of_living = db.Column(db.String(100))
    country_of_living = db.Column(db.String(100))
    type = db.Column(db.String(50))

    __mapper_args__ = {
        'polymorphic_identity': 'user',
        'polymorphic_on': type
    }

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'surname': self.surname,
            'id_type': self.id_type,
            'id_number': self.id_number,
            'city_of_living': self.city_of_living,
            'country_of_living': self.country_of_living,
            'type': self.type
        }

class Athlete(User):
    __tablename__ = 'athletes'  # To ensure clarity and maintain table consistency
    ethnicity = db.Column(db.String(50))
    age = db.Column(db.Integer)
    gender = db.Column(db.String(10))
    weight = db.Column(db.Float)
    height = db.Column(db.Float)
    city_of_birth = db.Column(db.String(100))
    country_of_birth = db.Column(db.String(100))
    sports = db.Column(db.String(200))  # Comma-separated list of sports
    profile_type = db.Column(db.String(50))
    heart_rate = db.Column(db.Integer)  # beats per minute
    vo2_max = db.Column(db.Float)       # maximal oxygen uptake
    blood_pressure = db.Column(db.String(50))  # e.g., '120/80'
    respiratory_rate = db.Column(db.Integer)  # breaths per minute
    training_frequency = db.Column(db.String(50))  # e.g., '3 times a week'
    sports_practiced = db.Column(db.String(200))   # e.g., 'Running, Swimming'
    average_session_duration = db.Column(db.Integer)  # in minutes
    recovery_time = db.Column(db.Integer)  # in hours, typical recovery period
    training_pace = db.Column(db.String(50))  # e.g., '7 min/km'
    plan_type = db.Column(db.String(50), default='basic')
    daily_calories = db.Column(db.Integer)  # kcal/day
    daily_protein = db.Column(db.Integer)  # grams/day
    daily_liquid = db.Column(db.Integer)  # ml/day
    daily_carbs = db.Column(db.Integer)  # grams/day
    meal_frequency = db.Column(db.String(50))  # e.g., '5 times a day'

    __mapper_args__ = {
        'polymorphic_identity': 'athlete',
    }

    def to_dict(self):
        base_dict = super().to_dict()
        athlete_specific_dict = {
            "age": self.age,
            "gender": self.gender,
            "weight": self.weight,
            "height": self.height,
            "city_of_birth": self.city_of_birth,
            "country_of_birth": self.country_of_birth,
            "sports": self.sports,
            "profile_type": self.profile_type,
            "heart_rate": self.heart_rate,
            "vo2_max": self.vo2_max,
            "blood_pressure": self.blood_pressure,
            "respiratory_rate": self.respiratory_rate,
            "training_frequency": self.training_frequency,
            "sports_practiced": self.sports_practiced,
            "average_session_duration": self.average_session_duration,
            "recovery_time": self.recovery_time,
            "training_pace": self.training_pace,
            "ethnicity": self.ethnicity,
            "plan_type": self.plan_type,
            "daily_calories": self.daily_calories,
            "daily_protein": self.daily_protein,
            "daily_liquid": self.daily_liquid,
            "daily_carbs": self.daily_carbs,
            "meal_frequency": self.meal_frequency
        }
        return {**base_dict, **athlete_specific_dict}

class ComplementaryServicesProfessional(User):
    __tablename__ = 'professionals'  # Ensuring distinct table mapping
    __mapper_args__ = {
        'polymorphic_identity': 'complementary_services_professional',
    }

class EventOrganizer(User):
    __tablename__ = 'organizers'  # Ensuring distinct table mapping
    __mapper_args__ = {
        'polymorphic_identity': 'event_organizer',
    }
