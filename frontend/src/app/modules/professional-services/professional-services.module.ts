import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../material.module';
import { ProfessionalDashboardComponent } from './components/professional-dashboard/professional-dashboard.component';
import { TrainingPlanComponent } from './components/training-plan/training-plan.component';

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ]
})
export class ProfessionalServicesModule {}
