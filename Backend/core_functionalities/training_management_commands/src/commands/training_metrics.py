from google.cloud import pubsub_v1
from datetime import datetime
from ..models.training_session import TrainingSession, db
import json

class TrainingCalculationHandler:
    def __init__(self):
        self.publisher = pubsub_v1.PublisherClient()
        self.topic_path = self.publisher.topic_path('miso-proyecto-de-grado-g09', 'metrics-events')

    def calculate_ftp_vo2max(self, session_id):
        # Fetch user training data
        print(session_id, "Value of session_id")
        session = TrainingSession.query.get(session_id)
        if not session:
            return {"error": "Session not found"}
        # Logic to calculate FTP and VO2max
        ftp = self._calculate_ftp(session.power_output, session.duration)
        vo2max = self._calculate_vo2max(session.max_heart_rate, session.resting_heart_rate)
        event_data = {
            "type": "TrainingMetricsCalculated",
            "data": {
                "session_id": str(session.id),
                "user_id": session.user_id,
                "ftp": ftp,
                "vo2max": vo2max,
                "timestamp": datetime.utcnow().isoformat()
            }
        }
        self.publisher.publish(self.topic_path, json.dumps(event_data).encode('utf-8'))

        return {"FTP": ftp, "VO2max": vo2max}


    def _calculate_ftp(self, power_output, duration):
        if duration > 0:
            return 95 * (power_output / duration)
        return 0

    def _calculate_vo2max(self, max_heart_rate, resting_heart_rate):
        if resting_heart_rate > 0:
            return 15 * (max_heart_rate / resting_heart_rate)
        return 0
