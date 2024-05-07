from flask import Blueprint, jsonify
from ..queries.get_events import GetEventsQueryHandler
from ..queries.get_event import GetEventQueryHandler
from ..queries.get_user_calendar import GetUserCalendarQueryHandler
from ..queries.get_events_published import GetEventsPublishedQueryHandler
from ..middlewares.auth import token_required

event_blueprint = Blueprint('event', __name__)

@event_blueprint.route('/events/get', methods=['GET'])
@token_required('event_organizer', 'complementary_services_professional', 'athlete')
def get_events():
    handler = GetEventsQueryHandler()
    events_data = handler.handle()
    return jsonify(events_data), 200

@event_blueprint.route('/events/<int:event_id>', methods=['GET'])
@token_required('event_organizer')
def get_event(event_id):
    handler = GetEventQueryHandler()
    event_data = handler.handle(event_id)
    if not event_data:
        return jsonify({'error': 'Event not found'}), 404
    return jsonify(event_data), 200

@event_blueprint.route('/events/published', methods=['GET'])
@token_required('event_organizer', 'athlete', 'complementary_services_professional')
def get_published_events():
    handler = GetEventsPublishedQueryHandler()
    events_data = handler.handle(status='published')
    return jsonify(events_data), 200

@event_blueprint.route('/user/<int:user_id>/calendar', methods=['GET'])
@token_required('athlete')
def get_user_calendar(user_id):
    query = GetUserCalendarQueryHandler(user_id=user_id)
    events = query.execute()
    return jsonify(events), 200