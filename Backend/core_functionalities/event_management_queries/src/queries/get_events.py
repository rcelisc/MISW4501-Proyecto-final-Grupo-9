from ..models.event import Event
from flask import g

class GetEventsQueryHandler:
    def handle(self):
        user_id = str(g.current_user_id)
        events = Event.query.filter_by(user_id=user_id).all()
        events_data = [{
            'id': event.id,
            'name': event.name,
            'description': event.description,
            'event_date': event.event_date.strftime('%Y-%m-%dT%H:%M:%S'),
            'duration': event.duration,
            'location': event.location,
            'category': event.category,
            'fee': event.fee,
            'status': event.status,
            'attendees': event.attendees,
            'additional_info': event.additional_info
        } for event in events]
        return events_data
    