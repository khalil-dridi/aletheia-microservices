import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/core/services/auth.service';
import { NotificationService, Notification } from 'src/app/core/services/notification.service';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {

  notifications: Notification[] = [];

  get unreadCount(): number {
    return this.notifications.filter(notification => !notification.read).length;
  }

  constructor(
    private authService: AuthService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {

    const user = this.authService.getUser();

    if (!user) {
      return;
    }

    this.notificationService
      .getUserNotifications(user.id)
      .subscribe({
        next: (data) => {
          this.notifications = data;
        },
        error: (err) => {
          console.error(err);
        }
      });
  }
}