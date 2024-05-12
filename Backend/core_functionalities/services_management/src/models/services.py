from sqlalchemy import Column, String, Float, Boolean, Integer
from ..extensions import db
from sqlalchemy.dialects.postgresql import JSON

class Service(db.Model):
    __tablename__ = 'services'
    id = Column(Integer, primary_key=True, autoincrement=True)
    name = Column(String, nullable=False)
    description = Column(String, nullable=False)
    rate = Column(Float, nullable=False)
    available = Column(Boolean, default=True)
    status = Column(String, default='created', nullable=False)
    clients = Column(JSON, default=lambda: {"users_ids": []})

    def __repr__(self):
        return f"<Service(name={self.name}, available={self.available})>"