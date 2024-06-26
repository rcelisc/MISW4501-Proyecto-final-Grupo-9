import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../../material.module';
import { AthleteDashboardComponent } from './components/athlete-dashboard/athlete-dashboard.component';
import { RouterModule } from '@angular/router';
import { SelectPlanComponent } from './components/select-plan/select-plan.component';
import { HttpClientModule } from '@angular/common/http';
import { DemographicInfoComponent } from './components/demographic-info/demographic-info.component';
import { SportInfoComponent } from './components/sport-info/sport-info.component';
import { AthleteCalendarComponent } from './components/athlete-calendar/athlete-calendar.component';
import { TrainingHistoryComponent } from './components/training-history/training-history.component';
import { FoodInfoComponent } from './components/food-info/food-info.component';
import { TranslateModule } from '@ngx-translate/core';
import { AppModule } from '../../app.module';
@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule,
    RouterModule,
    HttpClientModule,
  ]
})
export class AthleteModule {}
