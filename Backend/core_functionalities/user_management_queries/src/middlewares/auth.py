from flask import request, jsonify, g
import jwt
from functools import wraps
from ..models.user import User

def token_required(*allowed_roles):
    def decorator(f):
        @wraps(f)
        def decorated_function(*args, **kwargs):
            token = request.headers.get('Authorization')
            if not token:
                return jsonify({'message': 'Token is missing!'}), 403
            
            # Split the token from "Bearer" prefix
            parts = token.split()
            if parts[0].lower() != 'bearer' or len(parts) == 1:
                return jsonify({'message': 'Invalid token format'}), 401
            token = parts[1]

            try:
                data = jwt.decode(token, 'login_key', algorithms=['HS256'])
                current_user = User.query.get(data['user_id'])
                if current_user.type not in allowed_roles:
                    return jsonify({'message': 'Unauthorized to access this resource'}), 403
            except Exception as e:
                return jsonify({'message': 'Token is invalid or the user does not exist', 'error': str(e)}), 403

            g.current_user = current_user  # Store user in Flask's global g object
            return f(*args, **kwargs)

        return decorated_function
    return decorator
