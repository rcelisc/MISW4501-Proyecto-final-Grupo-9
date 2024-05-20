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
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'event-events-sub')

    def callback(self, message):
        try:
            logger.info(f"Received message ID: {message.message_id} with data: {message.data}")
            with self.app.app_context():  # Ensure app context is used within callback
                logger.info(f"Received message: {message.data}")
                message_data = json.loads(message.data.decode('utf-8'))
                message.ack()

                if message_data['type'] == 'EventCreated':
                    self.process_event_created(message_data)

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
    
    def process_event_created(self, message):
        print(f"Processing EventCreated: {message}")
        event_data = message['data']
        
        # Parse the event_date from string to datetime object
        if 'event_date' in event_data and isinstance(event_data['event_date'], str):
            print(f"Converting event_date to datetime object.")
            event_data['event_date'] = datetime.strptime(event_data['event_date'], '%Y-%m-%dT%H:%M:%S')
        try:
            with self.app.app_context():
                new_event = Event(**event_data)
                db.session.add(new_event)
                db.session.commit()
                print(f"Event {new_event.id} created in query service.")
        except Exception as e:
            print(f"Failed to create event in query service: {e}")
            db.session.rollback()
            

def start_listener_in_background(app):
    listener = EventUpdatesListener(app)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True 
    thread.start()