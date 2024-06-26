from flask import Blueprint, request, jsonify
from ..queries.get_services import get_services_and_events
from ..queries.get_services_published import get_services_and_events_published
from ..queries.get_services_by_user import get_user_purchased_services
from ..commands.create_service import create_service
from ..commands.publish_service import publish_service
from ..commands.purchase_service import purchase_service
from ..middlewares.auth import token_required

service_blueprint = Blueprint('service', __name__)

@service_blueprint.route('/services', methods=['GET'])
@token_required('complementary_services_professional')
def get_services():
    services, events = get_services_and_events()
    return jsonify({
        'services': services,
        'events': events
    })

@service_blueprint.route('/services/published', methods=['GET'])
@token_required('complementary_services_professional', 'athlete')
def get_services_published():
    services, events = get_services_and_events_published()
    return jsonify({
        'services': services,
        'events': events
    })

@service_blueprint.route('/services', methods=['POST'])
@token_required('complementary_services_professional')
def create_services():
    data = request.json
    service_id = create_service(data)
    return jsonify({"message": "Service created successfully", "service_id": service_id}), 201

@service_blueprint.route('/services/<int:service_id>/publish', methods=['POST'])
@token_required('complementary_services_professional')
def publish_service_endpoint(service_id):
    try:
        publish_service(service_id)
        return jsonify({"message": "Service published successfully"}), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400
    
@service_blueprint.route('/services/<int:service_id>/purchase', methods=['POST'])
@token_required('athlete')
def purchase_service_endpoint(service_id):
    user_id = request.json.get('user_id')
    try:
        result = purchase_service(user_id, service_id)
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400

@service_blueprint.route('/services/user/<int:user_id>', methods=['GET'])
@token_required('athlete')
def get_services_purchased_by_user(user_id):
    try:
        result = get_user_purchased_services(user_id)
        return jsonify(result), 200
    except ValueError as e:
        return jsonify({"error": str(e)}), 400

