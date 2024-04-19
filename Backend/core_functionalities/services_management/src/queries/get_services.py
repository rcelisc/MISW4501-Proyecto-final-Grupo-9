from ..models.services import Service
from ..extensions import db
import requests
import os

def get_services_and_events():
    services = Service.query.all()
    events = fetch_events()  # Assuming external API call
    return services, events

def fetch_events():
    # Set a default URL in case the environment variable is not set
    base_url = os.getenv('EVENTS_SERVICE_URL', 'http://event_management_queries_container:3002/events/get')
    # CLOUD URL
    # response = requests.get('http://event-management-queries.default.svc.cluster.local:3002/events/get')
    # Build the full URL for fetching events
    full_url = f'{base_url}/events/get'

    # Make the request and return the response
    response = requests.get(full_url)
    return response.json()  