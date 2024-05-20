
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
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    const requiredRoles = route.data['roles'] as Array<string>; // Define required roles in route data
    const userRole = localStorage.getItem('userRole');

    if (!userRole || (requiredRoles && !requiredRoles.includes(userRole))) {
      this.router.navigate(['/unauthorized']);
      return false;
    }
    return true;
  }
}

export const routes: Routes = [
  {path: '', component:WelcomePageComponent},
  {path: 'unauthorized', component: UnauthorizedComponent},
  {path: 'training-plan', component: TrainingPlanComponent, canActivate: [RoleGuard], data: {roles: ['complementary_services_professional']}},
  {path: 'training-history', component: TrainingHistoryComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'create-service', component: CreateServiceComponent, canActivate: [RoleGuard], data: {roles: ['complementary_services_professional']}},
  {path: 'service-list', component: ServiceListComponent, canActivate: [RoleGuard], data: {roles: ['complementary_services_professional']}},
  {path: 'service-published-list', component: ServicePublishedListComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'create-event', component: CreateEventComponent, canActivate: [RoleGuard], data: {roles: ['event_organizer']}},
  {path: 'event-list', component: EventListComponent, canActivate: [RoleGuard], data: {roles: ['event_organizer']}},
  {path: 'login', component: LoginComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'athlete-dashboard', component: AthleteDashboardComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'professional-dashboard', component: ProfessionalDashboardComponent, canActivate: [RoleGuard], data: {roles: ['complementary_services_professional']}},
  {path: 'organizer-dashboard', component: EventOrganizerDashboardComponent, canActivate: [RoleGuard], data: {roles: ['event_organizer']}},
  {path: 'select-plan', component: SelectPlanComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'sport-info', component: SportInfoComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'food-info', component: FoodInfoComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'demographic-info', component: DemographicInfoComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'event-calendar', component: EventCalendarComponent, canActivate: [RoleGuard], data: {roles: ['event_organizer']}},
  {path: 'athlete-calendar', component: AthleteCalendarComponent, canActivate: [RoleGuard], data: {roles: ['athlete']}},
  {path: 'meal-plan', component: MealPlanComponent, canActivate: [RoleGuard], data: {roles: ['complementary_services_professional']}}
];
