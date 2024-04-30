import { Component, OnInit } from '@angular/core';
import { NotificationService, Notification } from '../../services/notification.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-notification-manager',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationManagerComponent implements OnInit {
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.notificationService.notifications.subscribe((notification:any) => {
      if (notification.message) {
        this.notifications.push(notification);
      } else {
        this.notifications = this.notifications.filter(n => n.id !== notification.id);
      }
    });
  }

  dismissNotification(id: number): void {
    this.notificationService.dismissNotification(id);
  }
}