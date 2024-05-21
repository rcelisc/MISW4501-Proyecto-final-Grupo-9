from google.cloud import pubsub_v1
from flask import current_app
from ..models.user import User, db, Athlete, ComplementaryServicesProfessional, EventOrganizer
import json
import logging
import threading

class EventUpdatesListener:
    def __init__(self,app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'user-events-sub')
    
    def callback(self, message):
        with self.app.app_context():  # Ensure app context is used within callback
            logging.info(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            if message_data['type'] == 'UserCreated':
                self.process_user_created(message_data)

    
    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        logging.info(f"Listening for messages on {self.subscription_path}")

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except Exception as e:
                streaming_pull_future.cancel()
                logging.error(f"Listening stopped due to error: {e}")
                streaming_pull_future.result()

    
    def process_user_created(self, message):
        user_data = message['data']
        user_type = message['data']['type']
        user_classes = {
            'athlete': Athlete,
            'complementary_services_professional': ComplementaryServicesProfessional,
            'event_organizer': EventOrganizer
        }
        user_class = user_classes.get(user_type, Athlete)
        try:
            new_user = user_class(**user_data)
            db.session.add(new_user)
            db.session.commit()
            logging.info(f"User {new_user.id} created in query service.")
        except Exception as e:
            logging.error(f"Failed to create user in query service: {e}")
            db.session.rollback()

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True
    thread.start()