import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../shared/material.module';
import { AthleteDashboardComponent } from './components/athlete-dashboard/athlete-dashboard.component';
import { RouterModule } from '@angular/router';
import { SelectPlanComponent } from './components/select-plan/select-plan.component';
import { HttpClientModule } from '@angular/common/http';
import { DemographicInfoComponent } from './components/demographic-info/demographic-info.component';
import { SportInfoComponent } from './components/sport-info/sport-info.component';

@NgModule({
  declarations: [
    AthleteDashboardComponent, SelectPlanComponent, DemographicInfoComponent, SportInfoComponent],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    RouterModule,
    HttpClientModule
  ]
})
export class AthleteModule {}
