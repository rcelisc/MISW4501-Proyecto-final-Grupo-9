from ..models.user import User, Athlete, ComplementaryServicesProfessional, EventOrganizer, db

class GetUsersQueryHandler:
    def handle(self):
        users = User.query.all()
        return [user.to_dict() for user in users]

class GetUserByIdQueryHandler:
    def handle(self, user_id):
        user = User.query.get(user_id)
        return user.to_dict() if user else None