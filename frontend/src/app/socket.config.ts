import { SocketIoConfig } from 'ngx-socket-io';

export const config: SocketIoConfig = { url: 'http://localhost:3007', options: { transports: ['websocket', 'polling'] } };
