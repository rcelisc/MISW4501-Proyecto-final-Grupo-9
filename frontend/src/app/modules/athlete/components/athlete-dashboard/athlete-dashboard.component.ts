import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../../../../material.module';
import { AuthService } from '../../../../services/auth.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-athlete-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, MaterialModule, TranslateModule],
  templateUrl: './athlete-dashboard.component.html',
  styleUrls: ['./athlete-dashboard.component.scss']
})
export class AthleteDashboardComponent implements OnInit {
  dashboardItems: any[] = [];
  userData: any;
  // Role mappings
  roleMappings: { [key: string]: string } = {
    'athlete': 'userTypeAthlete',
    'complementary_services_professional': 'userTypeProfessional',
    'event_organizer': 'userTypeOrganizer'
  };

  constructor(private authService: AuthService, private translate: TranslateService) {
    this.translate.setDefaultLang('en');
  }

  switchLanguage(language: string) {
    this.translate.use(language);
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
            this.userData.translatedRole = this.roleMappings[data.type];
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
      { titleKey: 'viewCalendar', contentKey: 'viewCalendarContent', link: '/athlete-calendar' },
      { titleKey: 'viewEventsServices', contentKey: 'viewEventsServicesContent', link: '/service-published-list' },
      { titleKey: 'viewTrainingHistory', contentKey: 'viewTrainingHistoryContent', link: '/training-history' },
      { titleKey: 'selectPlan', contentKey: 'selectPlanContent', link: '/select-plan' },
      { titleKey: 'addDemographicInfo', contentKey: 'addDemographicInfoContent', link: '/demographic-info' },
      { titleKey: 'addFoodInfo', contentKey: 'addFoodInfoContent', link: '/food-info' },
      { titleKey: 'addSportInfo', contentKey: 'addSportInfoContent', link: '/sport-info' }
    ];
  }
}
