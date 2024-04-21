from flask_socketio import emit

def setup_socket_events(socketio):

    @socketio.on('connect')
    def handle_connect():
        print("Client connected")
        emit('connection_status', {'data': 'Connected to server'})

    @socketio.on('disconnect')
    def handle_disconnect():
        print("Client disconnected")
