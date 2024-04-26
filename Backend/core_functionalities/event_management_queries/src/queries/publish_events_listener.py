from google.cloud import pubsub_v1
from flask import current_app
from datetime import datetime

from ..logger import configure_logging
from ..models.event import Event, db
from sqlalchemy.orm.attributes import flag_modified
import json
import threading
import time
logger = configure_logging()
class EventUpdatesListener:
    def __init__(self, app):
        self.app = app
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'event-publish-events-sub')

    def callback(self, message):
        try:
            logger.info(f"Received message ID: {message.message_id} with data: {message.data}")
            with self.app.app_context():  # Ensure app context is used within callback
                logger.info(f"Received message: {message.data}")
                message_data = json.loads(message.data.decode('utf-8'))
                message.ack()

                if message_data['type'] == 'EventPublished':
                    self.process_event_published(message_data)
        except Exception as e:
            logger.info(f"Failed to process message {message.message_id}: {str(e)}")
    
    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        logger.info("Listening for messages on {}".format(self.subscription_path))
        # Wrap subscriber in a with-block to automatically call close() when done.
        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()  # Trigger the shutdown.
                streaming_pull_future.result()  # Block until the shutdown is complete.

    def process_event_published(self, message):
        print(f"Processing EventPublished: {message}")
        event_id = message['event_id']
        event = Event.query.get(event_id)
        if not event:
            print(f"Event {event_id} not found.")
            return

        if event.status != 'created':
            print(f"Event {event_id} is not in a state that can be published.")
            return

        event.status = 'published'
        flag_modified(event, "status")
        try:
            db.session.commit()
            print(f"Event {event_id} published in query service.")
        except Exception as e:
            print(f"Failed to publish event in query service: {e}")
            db.session.rollback()

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True  # This ensures the thread doesn't prevent the app from exiting
    thread.start()