import json
from google.cloud import pubsub_v1
from flask import current_app
from ..models.user import Athlete, db
import threading

class EventUpdatesListener:
    def __init__(self, app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        # Adjust the subscription path to listen for user plan update events
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'user-plan-updated-sub')
    
    def callback(self, message):
        with self.app.app_context():  # Ensure Flask app context for DB session
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            # Handling user plan updates
            if message_data['type'] == 'UserPlanUpdated':
                self.process_user_plan_updated(message_data)

    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        print("Listening for messages on {}".format(self.subscription_path))

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()  # Trigger the shutdown.
                streaming_pull_future.result()  # Block until the shutdown is complete.

    def process_user_plan_updated(self, message):
        user_id = message['data']['user_id']
        plan_type = message['data']['plan_type']
        user = Athlete.query.get(user_id)
        if not user:
            print("User not found")
            return

        # Update the user with new plan type
        user.plan_type = plan_type
        
        try:
            db.session.commit()
            print(f"Plan type updated for user {user.id} to {plan_type}.")
        except Exception as e:
            db.session.rollback()
            print(f"Failed to update plan type for user {user.id}: {e}")

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True  # This ensures the thread doesn't prevent the app from exiting
    thread.start()
