from ..models.services import Service, db
import requests
import os

def get_services_and_events_published():
    services_data = Service.query.filter_by(status='published').all()
    services = [{
            'id': service.id,
            'name': service.name,
            'description': service.description,
            'available': service.available,
            'status': service.status,
            'rate': service.rate
        } for service in services_data]
    events = fetch_events()  # Assuming external API call
    return services, events

def fetch_events():
    # Set a default URL in case the environment variable is not set
    base_url = os.getenv('EVENTS_SERVICE_URL', 'http://event_management_queries_container:3002')
    # CLOUD URL
    # response = requests.get('http://event-management-queries.default.svc.cluster.local:3002/events/published')
    # Build the full URL for fetching events
    full_url = f'{base_url}/events/published'

    # Make the request and return the response
    response = requests.get(full_url)
    return response.json()  