from google.cloud import pubsub_v1
import json
from datetime import datetime
from ..models.training_session import TrainingSession, db
import time
import threading
from uuid import UUID

class EventUpdatesListener():
    def __init__(self, app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'metrics-events-sub')

    def callback(self, message):
        with self.app.app_context():
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            if message_data['type'] == 'TrainingMetricsCalculated':
                self.update_training_session_metrics(message_data['data'])

    def update_training_session_metrics(self, data):
        try:
            session_id = data['session_id']
            uuid_session_id = UUID(session_id)  # Ensuring the session_id is a valid UUID
            session = TrainingSession.query.get(uuid_session_id)
            if session:
                session.ftp = data['ftp']
                session.vo2max = data['vo2max']
                db.session.commit()
                print(f"Updated session {session.id} with FTP: {session.ftp}, VO2max: {session.vo2max}")
            else:
                print("Session not found")
        except ValueError:
            print("Invalid UUID format")
        except KeyError:
            print("Key error - check data keys")
        except Exception as e:
            print(f"Unexpected error: {e}")
    
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