import { Injectable } from '@angular/core';
import { Socket, SocketIoConfig } from 'ngx-socket-io';

const config: SocketIoConfig = { 
 //url: 'http://localhost:3007', 
 url: 'http://35.232.6.198',
 options: { transports: ['websocket', 'polling'] }
};

@Injectable({
 providedIn: 'root'
})
export class SocketService extends Socket {

 constructor() {
    super(config);
 }

 // Connect to the WebSocket
 override connect() {
    super.connect();
 }

 // Disconnect from the WebSocket
 override disconnect() {
    super.disconnect();
 }

 // Generic method to listen to any event
 listen(eventName: string) {
    return super.fromEvent<any>(eventName);
 }

 // Generic method to listen to any event
 override fromEvent<T>(eventName: string) {
    return super.fromEvent<T>(eventName);
 }

 // Listen for connection status
 getConnectionStatus() {
    return super.fromEvent<any>('connection_status');
 }
}
