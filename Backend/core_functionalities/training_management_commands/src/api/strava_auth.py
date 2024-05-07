from flask import Blueprint, request, redirect, session, url_for, jsonify
import requests
import os
from ..middlewares.auth import token_required

strava_auth_blueprint = Blueprint('strava_auth', __name__)

CLIENT_ID = os.getenv('STRAVA_CLIENT_ID')
CLIENT_SECRET = os.getenv('STRAVA_CLIENT_SECRET')
REDIRECT_URI = os.getenv('STRAVA_REDIRECT_URI')

@strava_auth_blueprint.route('/authorize_strava')
@token_required( 'athlete')
def authorize_strava():
    strava_auth_url = f"https://www.strava.com/oauth/authorize?client_id={CLIENT_ID}&response_type=code&redirect_uri={REDIRECT_URI}&approval_prompt=force&scope=read,activity:read"
    return redirect(strava_auth_url)

@strava_auth_blueprint.route('/strava_callback')
def strava_callback():
    code = request.args.get('code')
    token_exchange_url = 'https://www.strava.com/oauth/token'
    data = {
        'client_id': CLIENT_ID,
        'client_secret': CLIENT_SECRET,
        'code': code,
        'grant_type': 'authorization_code'
    }
    response = requests.post(token_exchange_url, data=data)
    access_token = response.json().get('access_token')
    session['strava_access_token'] = access_token
    return "Strava account successfully linked!"
