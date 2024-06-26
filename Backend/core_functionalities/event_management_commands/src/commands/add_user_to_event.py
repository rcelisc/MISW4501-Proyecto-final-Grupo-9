from google.cloud import pubsub_v1
from ..models.event import Event, db
from sqlalchemy.orm.attributes import flag_modified
import json
from ..logger import configure_logging
logger = configure_logging()
class AddUserToEventCommand:
    def __init__(self, user_id, event_id):
        self.user_id = user_id
        self.event_id = event_id
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'add-user-events')


    def execute(self):
        event = Event.query.get(self.event_id)
        if not event:
            raise ValueError("Event not found")
        if "user_ids" not in event.attendees:
            logger.info("attendees not in event")
            event.attendees["user_ids"] = []
        if self.user_id not in event.attendees["user_ids"]:
            logger.info("user not in attendees")
            event.attendees["user_ids"].append(self.user_id)
            flag_modified(event, "attendees")
            db.session.commit()
            message = {
                "event_id": self.event_id,
                "user_id": self.user_id,
                "type": "UserAddedToEvent"
            }
            # Data must be a byte string
            future = self.publisher.publish(self.topic_path, json.dumps(message).encode('utf-8'))
            logger.info(f"Published message ID: {future.result()}")