from flask import request
from ..models.services import Service
from ..extensions import db
import requests
import os

def get_services_and_events():
    services_data = Service.query.all()
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
    token = request.headers.get('Authorization') 
    # Set a default URL in case the environment variable is not set
    # base_url = os.getenv('EVENTS_SERVICE_URL', 'http://event_management_queries_container:3002')
    # CLOUD URL
    base_url = 'http://event-management-queries.default.svc.cluster.local:3002'
    # Build the full URL for fetching events
    full_url = f'{base_url}/events/get'
    headers = {'Authorization': token}
    # Make the request and return the response
    response = requests.get(full_url, headers=headers)
    if response.status_code == 200:
        return response.json()
    else:
        return [] 