from google.cloud import pubsub_v1
from datetime import datetime
import json
from ..models.training_session import TrainingSession, db
import time
import threading


class EventUpdatesListener:
    def __init__(self, app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'training-events-sub')
    
    def callback(self, message):
        with self.app.app_context():
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            if message_data['type'] == 'TrainingSessionStopped':
                self.update_query_model(message_data.get('data', {}))

    def update_query_model(self, event_data):
        print("Processing data:", event_data)

        try:
            new_session = TrainingSession(
                session_id=event_data['session_id'],
                user_id=event_data['user_id'],
                end_time=datetime.fromisoformat(event_data['end_time']),
                duration=event_data['duration'],
                training_type=event_data.get('training_type', 'default'),
                calories_burned=event_data.get('calories_burned', 0),
                notes=event_data.get('notes', '')
            )
            db.session.add(new_session)
            db.session.commit()
            print("Training session updated in DB.")
        except Exception as e:
            db.session.rollback()
            print(f"Failed to update session data: {e}")
    
    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        print("Listening for messages on {}".format(self.subscription_path))

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()
                streaming_pull_future.result()

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True
    thread.start()