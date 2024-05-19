import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-event-organizer-dashboard',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule, TranslateModule],
  templateUrl: './event-organizer-dashboard.component.html',
  styleUrls: ['./event-organizer-dashboard.component.scss']
})
export class EventOrganizerDashboardComponent implements OnInit {
  dashboardItems: any[] = [];
  userData: any;

  constructor(private authService: AuthService, private translate: TranslateService) {
    this.translate.setDefaultLang('en');
  }

  ngOnInit() {
    this.fetchUserData();
    this.setDashboardItems();
  }

  fetchUserData() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.authService.getUserById(decodedToken.user_id).subscribe({
          next: (data) => {
            this.userData = data;
          },
          error: (err) => {
            console.error('Failed to fetch user data:', err);
          }
        });
      } else {
        console.error('Token is invalid or expired');
      }
    }
  }

  setDashboardItems() {
    this.dashboardItems = [
      { titleKey: 'createEvents', contentKey: 'createEventsContent', link: '/create-event' },
      { titleKey: 'viewAndPublishEvents', contentKey: 'viewAndPublishEventsContent', link: '/event-list' },
      { titleKey: 'viewEventCalendar', contentKey: 'viewEventCalendarContent', link: '/event-calendar' }
    ];
  }
}
