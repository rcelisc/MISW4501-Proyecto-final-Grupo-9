
import { Routes } from '@angular/router';
import { ServiceListComponent } from './modules/create-service/components/service-list/service-list.component';
import { CreateServiceComponent } from './modules/create-service/components/create-service/create-service.component';
import { CreateEventComponent } from './modules/create-event/components/create-event/create-event.component';
import { EventListComponent } from './modules/create-event/components/event-list/event-list.component';
import { LoginComponent } from './modules/auth/components/login/login.component';
import { RegisterComponent } from './modules/auth/components/register/register.component';
import { WelcomePageComponent } from './core/components/welcome-page/welcome-page.component';
import { AthleteDashboardComponent } from './modules/athlete/components/athlete-dashboard/athlete-dashboard.component';
import { ProfessionalDashboardComponent } from './modules/professional-services/components/professional-dashboard/professional-dashboard.component';
import { EventOrganizerDashboardComponent } from './modules/event-organizer/components/event-organizer-dashboard/event-organizer-dashboard.component';
import { SelectPlanComponent } from './modules/athlete/components/select-plan/select-plan.component';
import { SportInfoComponent } from './modules/athlete/components/sport-info/sport-info.component';
import { DemographicInfoComponent } from './modules/athlete/components/demographic-info/demographic-info.component';
import { EventCalendarComponent } from './modules/event-organizer/components/event-calendar/event-calendar.component';
import { ServicePublishedListComponent } from './modules/create-service/components/service-published-list/service-published-list.component';
import { AthleteCalendarComponent } from './modules/athlete/components/athlete-calendar/athlete-calendar.component';
import { TrainingPlanComponent } from './modules/professional-services/components/training-plan/training-plan.component';
import { MealPlanComponent } from './modules/professional-services/components/meal-plan/meal-plan.component';
import { TrainingHistoryComponent } from './modules/athlete/components/training-history/training-history.component';
import { FoodInfoComponent } from './modules/athlete/components/food-info/food-info.component';
import { UnauthorizedComponent } from './core/components/unauthorized/unauthorized.component';

export const routes: Routes = [
  {path: '', component:WelcomePageComponent},
  {path: 'unauthorized', component: UnauthorizedComponent},
  {path: 'training-plan', component: TrainingPlanComponent},
  {path: 'training-history', component: TrainingHistoryComponent},
  {path: 'create-service', component: CreateServiceComponent},
  {path: 'service-list', component: ServiceListComponent},
  {path: 'service-published-list', component: ServicePublishedListComponent},
  {path: 'create-event', component: CreateEventComponent},
  {path: 'event-list', component: EventListComponent},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'athlete-dashboard', component: AthleteDashboardComponent},
  {path: 'professional-dashboard', component: ProfessionalDashboardComponent},
  {path: 'organizer-dashboard', component: EventOrganizerDashboardComponent},
  {path: 'select-plan', component: SelectPlanComponent},
  {path: 'sport-info', component: SportInfoComponent},
  {path: 'food-info', component: FoodInfoComponent},
  {path: 'demographic-info', component: DemographicInfoComponent},
  {path: 'event-calendar', component: EventCalendarComponent},
  {path: 'athlete-calendar', component: AthleteCalendarComponent},
  {path: 'meal-plan', component: MealPlanComponent}
];
