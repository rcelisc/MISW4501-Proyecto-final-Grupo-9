import json
from google.cloud import pubsub_v1
from ..models.event import Event, db
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm.attributes import flag_modified

class PublishEventCommandHandler:
    def __init__(self):
        # Initialize Pub/Sub publisher
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'event-publish-events')

    def handle(self, event_id):
        event = Event.query.get(event_id)
        if not event:
            raise ValueError("Event not found")
        if event.status not in ['created']:
            raise ValueError("Event is not in a state that can be published")

        event.status = 'published'
        flag_modified(event, "status")
        try:
            db.session.commit()

            # Publish an event to Pub/Sub
            message = {
                "type": "EventPublished",
                "event_id": event.id,
                "status": "published"
            }
            self.publisher.publish(self.topic_path, json.dumps(message).encode('utf-8'))
        except IntegrityError:
            db.session.rollback()
            raise ValueError("Failed to update event status")
