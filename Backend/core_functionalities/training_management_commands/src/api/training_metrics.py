from flask import Blueprint, request, jsonify
from ..commands.training_metrics import TrainingCalculationHandler
from ..middlewares.auth import token_required

training_metrics_blueprint = Blueprint('training_metrics', __name__)

@training_metrics_blueprint.route('/calculate-ftp-vo2max', methods=['POST'])
@token_required('athlete')
def calculate_ftp_vo2max():
    session_id = request.json.get('session_id')
    handler = TrainingCalculationHandler()
    results = handler.calculate_ftp_vo2max(session_id)
    return jsonify(results), 200