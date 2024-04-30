import { TestBed } from '@angular/core/testing';
import { NotificationService, Notification } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NotificationService]
    });
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should show a notification', () => {
    const message = 'Test notification message';

    service.notifications.subscribe((notification: Notification) => {
      expect(notification.message).toBe(message);
    });

    service.showNotification(message);
  });

  it('should dismiss a notification', () => {
    const messageId = 1;

    service.notifications.subscribe((notification: Notification) => {
      expect(notification.id).toBe(messageId);
      expect(notification.message).toBe('');
    });

    service.dismissNotification(messageId);
  });
});
