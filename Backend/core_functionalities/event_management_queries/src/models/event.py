from flask_sqlalchemy import SQLAlchemy
from ..extensions import db
from sqlalchemy.dialects.postgresql import JSON

class Event(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)
    description = db.Column(db.Text, nullable=False)
    event_date = db.Column(db.DateTime, nullable=False)
    duration = db.Column(db.Integer, nullable=False)
    location = db.Column(db.String(255), nullable=False)
    category = db.Column(db.String(255), nullable=True)
    fee = db.Column(db.Float, nullable=True)
    attendees = db.Column(JSON, default=lambda: {"user_ids": []})
    status = db.Column(db.String(50), default='created', nullable=False)
    additional_info = db.Column(JSON)  # This column can store any additional JSON-structured info
    user_id = db.Column(db.String(50), nullable=False)

    def __repr__(self):
        return f'<Event {self.name}>'
    