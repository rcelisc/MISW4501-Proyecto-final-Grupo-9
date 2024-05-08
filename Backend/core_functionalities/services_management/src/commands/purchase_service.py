from ..models.services import Service, db
from sqlalchemy.orm.attributes import flag_modified
import json

def purchase_service(user_id, service_id):
    service = Service.query.get(service_id)
    if not service:
        raise ValueError("Service not found")
    if not service.available:
        raise ValueError("Service is not available")

    if "user_ids" not in service.clients:
        service.clients["user_ids"] = []
    if user_id not in service.clients["user_ids"]:
        service.clients["user_ids"].append(user_id)
        flag_modified(service, "clients")
    
    try:
        db.session.commit()
        return {"message": "Service purchased successfully", "service_id": service_id}
    except Exception as e:
        db.session.rollback()
        raise e
