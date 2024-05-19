import { Component, OnInit } from '@angular/core';
import { NotificationService, Notification } from '../../services/notification.service';
import { CommonModule } from '@angular/common';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

@Component({
  selector: 'app-notification-manager',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss']
})
export class NotificationManagerComponent implements OnInit {
  notifications: Notification[] = [];

  constructor(private notificationService: NotificationService, private translate: TranslateService) {}

  ngOnInit(): void {
    this.notificationService.notifications.subscribe((notification: any) => {
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
