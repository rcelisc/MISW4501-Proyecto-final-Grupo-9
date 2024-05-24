from flask import jsonify, request
from ..models.services import Service, db
from sqlalchemy import cast
from sqlalchemy.dialects.postgresql import JSONB

def get_user_purchased_services(user_id):
    try:
        purchased_services = Service.query.filter(
            cast(Service.clients, JSONB)["user_ids"].contains([user_id])
        ).all()

        # Convert database model instances to dictionaries
        services_data = [{
            'id': service.id,
            'name': service.name,
            'description': service.description,
            'rate': service.rate,
            'available': service.available,
            'status': service.status
        } for service in purchased_services]

        return services_data  # Just return the list of dictionaries
    except Exception as e:
        # Log the error or handle it accordingly
        return {"error": str(e)}
