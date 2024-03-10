from flask import jsonify, request, Blueprint
from ..commands.create_user import CreateUser
from ..models.users import UserSchema


user_schema = UserSchema()
api_users = Blueprint('users', __name__)


@api_users.route('/api/user/create', methods = ['POST'])
def create_user():
    json = request.get_json()
    fields_request = ['username','password','email','dni','fullName','phoneNumber']

    for field in fields_request:
        if field not in json:
            json[field]=""
    
    result = CreateUser(json['username'],json['password'],json['email'],json['dni'],json['fullName'],json['phoneNumber']).execute()    
    return jsonify(result), 201