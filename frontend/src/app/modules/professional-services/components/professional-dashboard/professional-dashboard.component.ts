import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../../services/auth.service';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-professional-dashboard',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule, TranslateModule],
  templateUrl: './professional-dashboard.component.html',
  styleUrls: ['./professional-dashboard.component.scss']
})
export class ProfessionalDashboardComponent implements OnInit {
  dashboardItems = [
    { titleKey: 'createServicesTitle', contentKey: 'createServicesContent', link: '/create-service' },
    { titleKey: 'viewAndPublishServicesTitle', contentKey: 'viewAndPublishServicesContent', link: '/service-list' },
    { titleKey: 'createTrainingPlansTitle', contentKey: 'createTrainingPlansContent', link: '/training-plan' },
    { titleKey: 'createNutritionPlansTitle', contentKey: 'createNutritionPlansContent', link: '/meal-plan' },
  ];
  
  userData: any;

  constructor(private authService: AuthService, private translate: TranslateService) {
    this.translate.setDefaultLang('en');
  }

  ngOnInit() {
    this.fetchUserData();
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
}
