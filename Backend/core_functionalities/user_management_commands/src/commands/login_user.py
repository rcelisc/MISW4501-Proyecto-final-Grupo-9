from google.cloud import pubsub_v1
import jwt
from datetime import datetime, timedelta, timezone
from ..models.user import User, db
from werkzeug.security import check_password_hash
import json

class LoginUserCommandHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'login-events')

    def generate_token(self, user):
        token = jwt.encode({
            'user_id': user.id,
            'role': user.type,
            'exp': datetime.utcnow() + timedelta(hours=24)
        }, 'login_key', algorithm='HS256')
        return token
    
    def is_token_valid(self, token):
        try:
            payload = jwt.decode(token, 'login_key', algorithms=['HS256'])
            # Convert the expiration time to a datetime object for comparison
            token_expiration = datetime.fromtimestamp(payload['exp'], tz=timezone.utc)
            return token_expiration > datetime.now(timezone.utc)
        except jwt.ExpiredSignatureError:
            return False
        except jwt.InvalidTokenError:
            return False

    def handle_login(self, id_number, password):
        user = User.query.filter_by(id_number=id_number).first()

        if user and user.verify_password(password):
            # Check for existing active session
            if user.is_active_session and self.is_token_valid(user.current_token):
                return {'status': False, 'message': 'Another session is already active'}

            # Generate new token, update session flag, and current token
            new_token = self.generate_token(user)
            user.is_active_session = True
            user.current_token = new_token
            db.session.commit()

            # Publish an event for session start
            event_data = {
                'type': 'UserLogin',
                'user_id': user.id,
                'role': user.type
            }
            self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))

            # Update user active session flag
            user.active_session = True
            db.session.commit()

            return {'status': True, 'token': new_token, 'role': user.type}
        else:
            return {'status': False, 'message': 'Invalid username or password'}
