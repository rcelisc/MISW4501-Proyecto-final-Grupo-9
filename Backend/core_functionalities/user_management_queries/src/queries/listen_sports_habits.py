import json
from google.cloud import pubsub_v1
from flask import current_app
from ..models.user import db, Athlete
import threading

class EventUpdatesListener:
    def __init__(self, app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'sports-habit-events-sub')
    
    def callback(self, message):
        with self.app.app_context():
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            if message_data['type'] == 'SportsHabitDataUpdated':
                self.process_sports_habit_updated(message_data)

    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        print("Listening for messages on {}".format(self.subscription_path))

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()  # Trigger the shutdown.
                streaming_pull_future.result()  # Block until the shutdown is complete.

    def process_sports_habit_updated(self, message):
        user_id = message['data']['user_id']
        user = Athlete.query.get(user_id)
        if not user:
            print("User not found")
            return

        # Update the user with new sports habit data
        user.training_frequency = message['data'].get('training_frequency')
        user.sports_practiced = message['data'].get('sports_practiced')
        user.average_session_duration = message['data'].get('average_session_duration')
        user.recovery_time = message['data'].get('recovery_time')
        user.training_pace = message['data'].get('training_pace')
        
        try:
            db.session.commit()
            print(f"Sports habits updated for user {user.id}.")
        except Exception as e:
            db.session.rollback()
            print(f"Failed to update sports habits for user {user.id}: {e}")

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True  # This ensures the thread doesn't prevent the app from exiting
    thread.start()
