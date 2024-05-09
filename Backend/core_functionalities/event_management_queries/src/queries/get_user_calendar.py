from ..models.event import Event
from sqlalchemy import cast
from sqlalchemy.dialects.postgresql import JSONB

class GetUserCalendarQueryHandler:
    def __init__(self, user_id):
        self.user_id = user_id

    def execute(self):
        events = Event.query.filter(
            cast(Event.attendees, JSONB)["user_ids"].contains([self.user_id])
        ).all()
        print(f"Found events for user_id {self.user_id}: {events}")
        return [{
            'id': event.id,
            'fecha': event.event_date.strftime('%Y-%m-%d %H:%M'),
            'nombre': event.name,
            'ubicación': event.location,
            'descripción': event.description
        } for event in events]