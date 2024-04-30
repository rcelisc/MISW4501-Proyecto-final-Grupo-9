import { Injectable } from '@angular/core';
import { Subject, Observable } from 'rxjs';

export interface Notification {
  id: number;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notificationSource = new Subject<Notification>();
  private lastId = 0;

  get notifications(): Observable<Notification> {
    return this.notificationSource.asObservable();
  }

  showNotification(message: string): void {
    this.notificationSource.next({ id: ++this.lastId, message });
  }

  dismissNotification(id: number): void {
    this.notificationSource.next({ id, message: ''});
  }
}
