import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../shared/material.module';
import { ProfessionalDashboardComponent } from './components/professional-dashboard/professional-dashboard.component';

@NgModule({
  declarations: [ProfessionalDashboardComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ]
})
export class ProfessionalServicesModule {}
