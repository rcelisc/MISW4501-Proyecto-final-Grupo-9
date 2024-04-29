# commands/publish_service.py

from ..models.services import Service, db
from sqlalchemy.exc import IntegrityError

def publish_service(service_id):
    service = Service.query.get(service_id)
    if not service:
        raise ValueError("Service not found")
    if service.status != 'created':
        raise ValueError("Service is not in a state that can be published")

    service.status = 'published'
    try:
        db.session.commit()
    except IntegrityError:
        db.session.rollback()
        raise ValueError("Failed to update service status")
