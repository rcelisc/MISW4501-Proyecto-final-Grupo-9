from flask import Blueprint, request, jsonify, session
from datetime import datetime
from ..commands.training_session import StartTrainingSessionCommandHandler, StopTrainingSessionCommandHandler, ReceiveSessionDataCommandHandler
import requests
from ..models.strava_activity import StravaActivity, db
from ..middlewares.auth import token_required
from ..logger import configure_logging
logger = configure_logging()

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
    access_token = request.headers.get('Authorization')
    logger.info(f"Using access token: {access_token}")

    headers = {'Authorization': f"Bearer {access_token}"}
    activities_url = 'https://www.strava.com/api/v3/athlete/activities'
    response = requests.get(activities_url, headers=headers)

    logger.info(f"Strava activities response: {response.text}")
    
    # Log the response text for debugging
    logger.info(f"Strava activities response: {response.text}")
    print(f"Strava activities response: {response.text}")
    
    try:
        activities = response.json()
    except ValueError as e:
        logger.error(f"Error parsing JSON: {e}")
        return jsonify({'error': 'Error parsing JSON response from Strava'}), 500

    logger.info(f"Parsed activities: {activities}")

    if isinstance(activities, list):
        for activity in activities:
            existing_activity = StravaActivity.query.filter_by(id=activity['id']).first()
            if not existing_activity:
                new_activity = StravaActivity(
                    id=activity['id'],
                    athlete_id=activity.get('athlete', {}).get('id', None),
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
    else:
        logger.error("Expected a list of activities, got something else")
        return jsonify({'error': 'Unexpected response format from Strava'}), 500


@training_session_blueprint.route('/activities', methods=['GET'])
def get_activities():
    activities = StravaActivity.query.all()
    return jsonify([activity.to_dict() for activity in activities])