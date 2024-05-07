from google.cloud import pubsub_v1
from ..models.user import User, db
from werkzeug.security import check_password_hash
import json

class LogoutUserCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'logout-events')

    def handle_logout(self, user_id):
        user = User.query.get(user_id)
        if not user:
            return {'status': False, 'message': 'User not found'}

        user.is_active_session = False
        user.current_token = None  # Optionally reset the token
        db.session.commit()

        # Publish an event for session end
        event_data = {
            'type': 'UserLogout',
            'user_id': user_id
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))

        return {'status': True, 'message': 'Logout successful'}
