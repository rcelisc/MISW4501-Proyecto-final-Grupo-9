import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../shared/material.module';
import { AthleteDashboardComponent } from './components/athlete-dashboard/athlete-dashboard.component';
import { RouterModule } from '@angular/router';
import { SelectPlanComponent } from './components/select-plan/select-plan.component';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [
    AthleteDashboardComponent, SelectPlanComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    RouterModule,
    HttpClientModule
  ]
})
export class AthleteModule {}
