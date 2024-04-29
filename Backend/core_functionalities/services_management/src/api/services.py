from flask import Blueprint, request, jsonify
from ..queries.get_services import get_services_and_events
from ..queries.get_services_published import get_services_and_events_published
from ..commands.create_service import create_service
from ..commands.publish_service import publish_service

service_blueprint = Blueprint('service', __name__)

@service_blueprint.route('/services', methods=['GET'])
def get_services():
    services, events = get_services_and_events()
    return jsonify({
        'services': services,
        'events': events
    })

@service_blueprint.route('/services/published', methods=['GET'])
def get_services_published():
    services, events = get_services_and_events_published()
    return jsonify({
        'services': services,
        'events': events
    })

@service_blueprint.route('/services', methods=['POST'])
def create_services():
    data = request.json
    service_id = create_service(data)
    return jsonify({"message": "Service created successfully", "service_id": service_id}), 201

@service_blueprint.route('/services/<int:service_id>/publish', methods=['POST'])
def publish_service_endpoint(service_id):
    try:
        publish_service(service_id)
        return jsonify({"message": "Service published successfully"}), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
