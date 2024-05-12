from flask_sqlalchemy import SQLAlchemy
from ..extensions import db
from werkzeug.security import generate_password_hash, check_password_hash

class User(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(100), nullable=False)
    surname = db.Column(db.String(100))
    id_type = db.Column(db.String(50))
    id_number = db.Column(db.String(50), nullable=False, unique=True)
    city_of_living = db.Column(db.String(100))
    country_of_living = db.Column(db.String(100))
    type = db.Column(db.String(50))
    password_hash = db.Column(db.String(256))
    current_token = db.Column(db.String(255))
    is_active_session = db.Column(db.Boolean, default=False)

    __mapper_args__ = {
        'polymorphic_identity':'user',
        'polymorphic_on':type
    }

    @property
    def password(self):
        raise AttributeError('password is not a readable attribute')
    
    @password.setter
    def password(self, password):
        self.password_hash = generate_password_hash(password)

    def verify_password(self, password):
        return check_password_hash(self.password_hash, password)

class Athlete(User):
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
        'polymorphic_identity':'athlete',
    }

class ComplementaryServicesProfessional(User):
    __mapper_args__ = {
        'polymorphic_identity':'complementary_services_professional',
    }

class EventOrganizer(User):
    __mapper_args__ = {
        'polymorphic_identity':'event_organizer',
    }