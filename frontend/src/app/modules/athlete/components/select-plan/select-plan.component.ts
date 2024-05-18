import { Component, OnInit } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlanService } from '../../../../services/plan.service';
import { AuthService } from '../../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-select-plan',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule],
  templateUrl: './select-plan.component.html',
  styleUrl: './select-plan.component.scss'
})
export class SelectPlanComponent implements OnInit{
  dashboardItems = [
    { title: 'Plan Basico', content: 'Disfruta de todas las funcionalidades basicas para el registro de tus entrenamientos', planType: 'basic' },
    { title: 'Plan Intermedio', content: 'Disfruta de todas las funcionalidades de monitoreo peridico de tus indices de salud y deportivos', planType: 'intermediate' },
    { title: 'Plan Premium', content: 'Disfruta de atencion personalizada con nuestros servicios profesionales y atencion prioritaria', planType: 'premium' },
   ];
   
  userId: number = 0;

  constructor( 
  private snackBar: MatSnackBar, 
  private planService: PlanService,
  private authService: AuthService,
  private router: Router
  ) {}

  ngOnInit() {
    this.setUserIdFromToken();
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
 
   updatePlan(planType: string) {
    if (!this.userId) {
      this.snackBar.open('User not authenticated', 'Close', { duration: 2000 });
      return;
    }

    this.planService.updatePlan(this.userId, planType)
      .subscribe({
        next: (response) => {
          this.snackBar.open(`Plan updated to ${planType}`, 'Close', { duration: 2000 });
        },
        error: () => {
          this.snackBar.open('Failed to update plan', 'Close', { duration: 2000 });
        }
      });
   }

   goBack(): void {
    this.router.navigate(['/athlete-dashboard']);
  }
 }
