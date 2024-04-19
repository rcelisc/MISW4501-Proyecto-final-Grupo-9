from google.cloud import pubsub_v1
from ..models.user import Athlete, ComplementaryServicesProfessional, EventOrganizer, db
import json

class CreateUserCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'user-events')  

    def handle(self, data, user_type):
        user_classes = {
            'athlete': Athlete,
            'professional': ComplementaryServicesProfessional,
            'organizer': EventOrganizer
        }
        user_class = user_classes.get(user_type, Athlete)  # Default to Athlete
        user = user_class(**data)
        db.session.add(user)
        db.session.commit()

        event_data = {
            "type": "UserCreated",
            "data": {
                "type": user_type,
                "id": str(user.id),
                "name": user.name,
                "surname": user.surname,
                "id_type": user.id_type,
                "id_number": user.id_number,
                "city_of_living": user.city_of_living,
                "country_of_living": user.country_of_living,
                "age": user.age,
                "gender": user.gender,
                "weight": user.weight,
                "height": user.height,
                "city_of_birth": user.city_of_birth,
                "country_of_birth": user.country_of_birth,
                "sports": user.sports,
                "profile_type": user.profile_type
            }
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))
        return user.id
