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
    age = db.Column(db.Integer)
    gender = db.Column(db.String(10))
    weight = db.Column(db.Float)
    height = db.Column(db.Float)
    city_of_birth = db.Column(db.String(100))
    country_of_birth = db.Column(db.String(100))
    sports = db.Column(db.String(200))  # Comma-separated list of sports
    profile_type = db.Column(db.String(50))

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
            "profile_type": self.profile_type
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
