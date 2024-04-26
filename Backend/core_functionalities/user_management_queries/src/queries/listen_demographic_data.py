import json
from google.cloud import pubsub_v1
from flask import current_app
from ..models.user import db, Athlete
import threading

class EventUpdatesListener:
    def __init__(self, app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        # Adjust the subscription path as needed for demographic data events
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'demographic-data-events-sub')
    
    def callback(self, message):
        with self.app.app_context():  # Ensure Flask app context for DB session
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            # Handling demographic data updates
            if message_data['type'] == 'DemographicDataUpdated':
                self.process_demographic_data_updated(message_data)

    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        print("Listening for messages on {}".format(self.subscription_path))

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()  # Trigger the shutdown.
                streaming_pull_future.result()  # Block until the shutdown is complete.

    def process_demographic_data_updated(self, message):
        user_id = message['data']['user_id']
        user = Athlete.query.get(user_id)
        if not user:
            print("User not found")
            return

        # Update the user with new demographic data
        user.ethnicity = message['data'].get('ethnicity')
        user.heart_rate = message['data'].get('heart_rate')
        user.vo2_max = message['data'].get('vo2_max')
        user.blood_pressure = message['data'].get('blood_pressure')
        user.respiratory_rate = message['data'].get('respiratory_rate')
        
        try:
            db.session.commit()
            print(f"Demographic data updated for user {user.id}.")
        except Exception as e:
            db.session.rollback()
            print(f"Failed to update demographic data for user {user.id}: {e}")

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True  # This ensures the thread doesn't prevent the app from exiting
    thread.start()
