import json
from google.cloud import pubsub_v1
import threading

class NutritionPlanEventListener:
    def __init__(self, app, socketio):
        self.app = app
        self.socketio = socketio
        self.subscriber = pubsub_v1.SubscriberClient()
        self.subscription_path = self.subscriber.subscription_path('miso-proyecto-de-grado-g09', 'nutrition-plan-events-sub')

    def callback(self, message):
        with self.app.app_context():
            print(f"Received message: {message.data}")
            message_data = json.loads(message.data.decode('utf-8'))
            message.ack()

            if message_data['event_type'] == 'NutritionPlanCreated':
                self.process_event(message_data)

    def process_event(self, message_data):
        notification = {
            "message": "New nutrition plan created!",
            "details": f"Plan ID {message_data['plan_id']} for User ID {message_data['user_id']}"
        }
        # Emit the notification to all connected clients
        print("Emitting event to all connected clients:", notification)
        self.socketio.emit('nutrition_plan_notification', notification, callback=self.confirm_receipt)
    
    def confirm_receipt(self, data):
        if data:
            print("Confirmation received from client:", data)
        else:
            print("No confirmation received, potential action needed")

    def start_listening(self):
        streaming_pull_future = self.subscriber.subscribe(self.subscription_path, callback=self.callback)
        print("Listening for messages on {}".format(self.subscription_path))

        with self.subscriber:
            try:
                streaming_pull_future.result()  # Block indefinitely.
            except TimeoutError:
                streaming_pull_future.cancel()  # Trigger the shutdown.
                streaming_pull_future.result()  # Block until the shutdown is complete.

def start_nutrition_plan_listener(app, socketio):
    listener = NutritionPlanEventListener(app, socketio)
    thread = threading.Thread(target=listener.start_listening)
    thread.daemon = True  # This ensures the thread doesn't prevent the app from exiting
    thread.start()
