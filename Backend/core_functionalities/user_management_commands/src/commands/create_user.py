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
            'complementary_services_professional': ComplementaryServicesProfessional,
            'event_organizer': EventOrganizer
        }
        user_class = user_classes.get(user_type, Athlete)  # Default to Athlete
        user = user_class(**data)
        db.session.add(user)
        db.session.commit()

        event_data = self.create_event_data(user, user_type)
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))
        return user.id
    
    def create_event_data(self, user, user_type):
        # Define fields relevant to each user type
        fields_by_type = {
            'athlete': ['id', 'name', 'surname', 'id_type', 'id_number', 'city_of_living', 'country_of_living',
                        'age', 'gender', 'weight', 'height', 'city_of_birth', 'country_of_birth', 'sports', 'profile_type'],
            'complementary_services_professional': ['id', 'name', 'surname', 'id_type', 'id_number', 'city_of_living', 'country_of_living'],
            'event_organizer': ['id', 'name', 'surname', 'id_type', 'id_number', 'city_of_living', 'country_of_living']
        }

        # Build the data dictionary based on the defined fields
        event_data = {
            "type": "UserCreated",
            "data": {field: getattr(user, field) for field in fields_by_type.get(user_type, [])}
        }
        event_data['data']['type'] = user_type
        return event_data