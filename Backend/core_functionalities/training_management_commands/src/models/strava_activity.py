from ..extensions import db

class StravaActivity(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    athlete_id = db.Column(db.Integer, index=True)
    name = db.Column(db.String(255))
    distance = db.Column(db.Float)  # in meters
    moving_time = db.Column(db.Integer)  # in seconds
    elapsed_time = db.Column(db.Integer)  # in seconds
    total_elevation_gain = db.Column(db.Float)  # in meters
    type = db.Column(db.String(50))

    def to_dict(self):
        return {
            "id": self.id,
            "athlete_id": self.athlete_id,
            "name": self.name,
            "distance": self.distance,
            "moving_time": self.moving_time,
            "elapsed_time": self.elapsed_time,
            "total_elevation_gain": self.total_elevation_gain,
            "type": self.type
        }
