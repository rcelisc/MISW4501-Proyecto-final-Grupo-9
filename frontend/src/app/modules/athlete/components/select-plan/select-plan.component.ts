import { Component } from '@angular/core';
import { MaterialModule } from '../../../../shared/material.module';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PlanService } from '../../../../services/plan.service';

@Component({
  selector: 'app-select-plan',
  standalone: true,
  imports: [MaterialModule, CommonModule, RouterModule],
  templateUrl: './select-plan.component.html',
  styleUrl: './select-plan.component.scss'
})
export class SelectPlanComponent {
  dashboardItems = [
    { title: 'Plan Basico', content: 'Disfruta de todas las funcionalidades basicas para el registro de tus entrenamientos', planType: 'basic' },
    { title: 'Plan Intermedio', content: 'Disfruta de todas las funcionalidades de monitoreo peridico de tus indices de salud y deportivos', planType: 'intermediate' },
    { title: 'Plan Premium', content: 'Disfruta de atencion personalizada con nuestros servicios profesionales y atencion prioritaria', planType: 'premium' },
   ];
   userId: number = 1; // This should be dynamically set based on the logged-in user

   constructor( private snackBar: MatSnackBar, private planService: PlanService) {}
 
   updatePlan(planType: string) {
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
 }
