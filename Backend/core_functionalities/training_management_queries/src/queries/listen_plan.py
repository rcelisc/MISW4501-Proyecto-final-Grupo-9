from google.cloud import pubsub_v1
from flask import current_app
from datetime import datetime
from ..models.training_plan import TrainingPlan, db
from sqlalchemy.orm.attributes import flag_modified
import json
import threading
import time

class EventUpdatesListener:
    def __init__(self,app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'training-plan-events-sub')
    
    def callback(self, message):
        with self.app.app_context():  # Ensure app context is used within callback
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            if message_data['type'] == 'TrainingPlanCreated':
                self.process_plan_created(message_data)

    
    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        print("Listening for messages on {}".format(self.subscription_path))

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()  # Trigger the shutdown.
                streaming_pull_future.result()  # Block until the shutdown is complete.

    
    def process_plan_created(self, message):
        plan_data = message['data']
        try:
            new_training_plan = TrainingPlan(**plan_data)
            db.session.add(new_training_plan)
            db.session.commit()
            print(f"Plan {new_training_plan.id} created in query service.")
        except Exception as e:
            print(f"Failed to create training plan in query service: {e}")
            db.session.rollback()

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True  # This ensures the thread doesn't prevent the app from exiting
    thread.start()