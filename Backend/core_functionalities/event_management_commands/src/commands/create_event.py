from google.cloud import pubsub_v1
import json
from datetime import datetime, timedelta
from ..models.event import Event, db
from sqlalchemy.exc import IntegrityError
from ..logger import configure_logging
import logging
logger = configure_logging()

class CreateEventCommandHandler:
    
    def __init__(self):
        # Initialize Pub/Sub publisher
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'event-events')

    def validate_data(self, data):
        # Ensure all mandatory fields are present
        mandatory_fields = ['name', 'description', 'event_date', 'duration', 'location', 'category', 'fee']
        missing_fields = [field for field in mandatory_fields if field not in data]
        if missing_fields:
            raise ValueError(f"Missing mandatory field(s): {', '.join(missing_fields)}")

    def check_overlap(self, data, event_id=None):
        if isinstance(data['event_date'], str):
            event_start = datetime.strptime(data['event_date'], '%Y-%m-%dT%H:%M:%S')
        elif isinstance(data['event_date'], datetime):
            event_start = data['event_date']
        else:
            raise ValueError("event_date must be a string or datetime")
        event_end = event_start + timedelta(hours=data['duration'])

        # Fetch events that could potentially overlap
        potential_overlaps = Event.query.filter(
            Event.location == data['location'],
            Event.id != event_id,  # Exclude the current event if updating
            Event.event_date <= event_end,
            Event.event_date >= event_start - timedelta(hours=24) # Considering a 24-hour buffer before the event
        ).all()

        # Now filter in Python to check for actual overlap
        for event in potential_overlaps:
            existing_event_end = event.event_date + timedelta(hours=event.duration)
            if existing_event_end > event_start and event.event_date < event_end:
                raise ValueError("An event is already scheduled at this location during the specified timeframe.")

    def handle(self, data):
        self.validate_data(data)
        if isinstance(data['event_date'], str):
            data['event_date'] = datetime.strptime(data['event_date'], '%Y-%m-%dT%H:%M:%S')
        self.check_overlap(data)
        
        try:
            event = Event(**data)
            db.session.add(event)
            db.session.commit()

            # Ensuring event_date is a string
            kafka_data = data.copy()
            kafka_data['event_date'] = kafka_data['event_date'].isoformat() if 'event_date' in kafka_data else None

            # Publish an event to PubSub after successfully creating the event
            message = {
                "type": "EventCreated",
                "event_id": event.id,
                "data": kafka_data
            }

            # Publish an event to Pub/Sub
            future = self.publisher.publish(self.topic_path, json.dumps(message).encode('utf-8'))
            future.add_done_callback(self._publish_callback)

            return event.id
        except IntegrityError:
            db.session.rollback()
            raise ValueError("Failed to create event due to a database error.")
        
    def _publish_callback(self, future):
        try:
            message_id = future.result()
            logger.info(f"SOY LOGGER Message published to Pub/Sub with ID: {message_id}")
            logging.info(f"SOY LOGGING Message published to Pub/Sub with ID: {message_id}")
        except Exception as e:
            logger.error(f"Failed to publish message to Pub/Sub: {e}")
