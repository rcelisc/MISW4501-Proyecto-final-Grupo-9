import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../shared/material.module';
import { AthleteDashboardComponent } from './components/athlete-dashboard/athlete-dashboard.component';

@NgModule({
  declarations: [AthleteDashboardComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ]
})
export class AthleteModule {}
