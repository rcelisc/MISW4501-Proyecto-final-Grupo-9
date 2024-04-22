from ..models.event import Event

class GetEventsPublishedQueryHandler:
    def handle(self, status):
        events = Event.query.filter_by(status=status).all()
        events_data = [{
            'id': event.id,
            'name': event.name,
            'description': event.description,
            'event_date': event.event_date.strftime('%Y-%m-%dT%H:%M:%S'),
            'duration': event.duration,
            'location': event.location,
            'category': event.category,
            'fee': event.fee,
            'additional_info': event.additional_info
        } for event in events]
        return events_data