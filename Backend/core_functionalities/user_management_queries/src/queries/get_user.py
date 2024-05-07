from ..models.user import User, Athlete, ComplementaryServicesProfessional, EventOrganizer, db

class GetUsersQueryHandler:
    def __init__(self, user_type='athlete'):
        self.user_type = user_type

    def handle(self):
        users = User.query.filter_by(type=self.user_type).all()
        return [user.to_dict() for user in users]

class GetUserByIdQueryHandler:
    def handle(self, user_id):
        user = User.query.get(user_id)
        return user.to_dict() if user else None