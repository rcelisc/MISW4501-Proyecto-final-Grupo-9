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
        'polymorphic_identity':'user',
        'polymorphic_on':type
    }

class Athlete(User):
    age = db.Column(db.Integer)
    gender = db.Column(db.String(10))
    weight = db.Column(db.Float)
    height = db.Column(db.Float)
    city_of_birth = db.Column(db.String(100))
    country_of_birth = db.Column(db.String(100))
    sports = db.Column(db.String(200))  # Comma-separated list of sports
    profile_type = db.Column(db.String(50))

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