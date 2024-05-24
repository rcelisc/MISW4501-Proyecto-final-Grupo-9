from google.cloud import pubsub_v1
from ..models.user import User, db
from werkzeug.security import check_password_hash
import json

class LogoutUserCommandHandler:
    def __init__(self):
        pass

    def handle_logout(self, user_id):
        user = User.query.get(user_id)
        if not user:
            return {'status': False, 'message': 'User not found'}

        user.is_active_session = False
        user.current_token = None  # Optionally reset the token
        db.session.commit()

        return {'status': True, 'message': 'Logout successful'}
