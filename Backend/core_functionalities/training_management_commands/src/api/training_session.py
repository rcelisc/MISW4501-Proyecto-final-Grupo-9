from flask import Blueprint, request, jsonify, session
from datetime import datetime
from ..commands.training_session import StartTrainingSessionCommandHandler, StopTrainingSessionCommandHandler, ReceiveSessionDataCommandHandler
import requests
from ..models.strava_activity import StravaActivity, db
from ..middlewares.auth import token_required

training_session_blueprint = Blueprint('training_session', __name__)

@training_session_blueprint.route('/start-training', methods=['POST'])
@token_required('athlete')
def start_training_session():
    data = request.json
    handler = StartTrainingSessionCommandHandler()
    session_id = handler.start(data)
    return jsonify({"session_id": session_id}), 201

@training_session_blueprint.route('/stop-training', methods=['POST'])
@token_required('athlete')
def stop_training_session():
    data = request.json
    handler = StopTrainingSessionCommandHandler()
    session_id = handler.stop(data)
    return jsonify({"message": "Training session stopped successfully", "session_id": session_id}), 200

@training_session_blueprint.route('/receive_session-data', methods=['POST'])
@token_required('athlete')
def submit_session_data():
    data = request.json
    handler = ReceiveSessionDataCommandHandler()
    results = handler.receive(data)
    return jsonify(results), 200

@training_session_blueprint.route('/fetch_strava_activities')
def fetch_strava_activities():
    access_token = session.get('strava_access_token')
    headers = {'Authorization': f"Bearer {access_token}"}
    activities_url = 'https://www.strava.com/api/v3/athlete/activities'
    response = requests.get(activities_url, headers=headers)
    activities = response.json()

    for activity in activities:
        new_activity = StravaActivity(
            athlete_id=activity.get('athlete', {}).get('id', None),  # Handling nested dictionaries
            name=activity.get('name'),
            distance=activity.get('distance'),
            moving_time=activity.get('moving_time'),
            elapsed_time=activity.get('elapsed_time'),
            total_elevation_gain=activity.get('total_elevation_gain'),
            type=activity.get('type')
        )
        db.session.add(new_activity)
    db.session.commit()

    return jsonify(activities)

@training_session_blueprint.route('/activities', methods=['GET'])
@token_required('athlete', 'complementary_services_professional')
def get_activities():
    activities = StravaActivity.query.all()
    return jsonify([activity.to_dict() for activity in activities])