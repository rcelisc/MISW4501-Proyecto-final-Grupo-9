import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlanService } from '../../../../services/plan.service';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';
import { TranslateService, TranslateModule } from '@ngx-translate/core';


@Component({
  selector: 'app-select-plan',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule, TranslateModule],
  templateUrl: './select-plan.component.html',
  styleUrl: './select-plan.component.scss'
})
export class SelectPlanComponent implements OnInit{
  dashboardItems: any[] = [];
  userId: number = 0;

  constructor( 
  private snackBar: MatSnackBar, 
  private planService: PlanService,
  private authService: AuthService,
  private router: Router,
  private translate: TranslateService

  ) {this.translate.setDefaultLang('en');}

  switchLanguage(language: string) {
    this.translate.use(language);
  }

  ngOnInit() {
    this.setUserIdFromToken();
    this.setDashboardItems();
  }

  setUserIdFromToken() {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = this.authService.decodeToken(token);
      if (decodedToken && 'user_id' in decodedToken) {
        this.userId = decodedToken.user_id;
      } else {
        console.error('Token is invalid or expired');
        this.router.navigate(['/login']); // Redirect to login if the token is invalid or expired
      }
    } else {
      this.router.navigate(['/login']); // Redirect to login if there's no token
    }
  }

  setDashboardItems() {
    this.dashboardItems = [
      { titleKey: 'basicPlanTitle', contentKey: 'basicPlanContent', planType: 'basic' },
      { titleKey: 'intermediatePlanTitle', contentKey: 'intermediatePlanContent', planType: 'intermediate' },
      { titleKey: 'premiumPlanTitle', contentKey: 'premiumPlanContent', planType: 'premium' }
    ];
  }
 
  updatePlan(planType: string) {
    if (!this.userId) {
      this.translate.get('userNotAuthenticated').subscribe((res: string) => {
        this.snackBar.open(res, 'Close', { duration: 2000 });
      });
      return;
    }


    this.planService.updatePlan(this.userId, planType)
      .subscribe({
        next: (response) => {
          this.translate.get('planUpdated', { planType }).subscribe((res: string) => {
            this.snackBar.open(res, 'Close', { duration: 2000 });
          });
        },
        error: () => {
          this.translate.get('updatePlanFailed').subscribe((res: string) => {
            this.snackBar.open(res, 'Close', { duration: 2000 });
          });
        }
      });
  }

   goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
 }
