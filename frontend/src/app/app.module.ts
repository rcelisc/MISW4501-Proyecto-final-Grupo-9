import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { SocketIoModule } from 'ngx-socket-io';
import { config } from './socket.config';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TrainingPlanService } from './services/training-plan.service';
import { CreateServiceModule } from './modules/create-service/create-service.module';
import { MaterialModule } from './material.module';
import { SharedModule } from './shared/shared.module';
import { RouterModule } from '@angular/router';
import { routes } from './app.routes';
import { CreateServiceService } from './services/create-service.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { CreateEventModule } from './modules/create-event/create-event.module';
import { CreateEventService } from './services/create-event.service';
import { LocaleService } from './services/locale.service';
import { CoreModule } from './core/core.module';
import { AuthModule } from './modules/auth/auth.module';
import { AuthService } from './services/auth.service';
import { AthleteModule } from './modules/athlete/athlete.module';
import { EventOrganizerModule } from './modules/event-organizer/event-organizer.module';
import { ProfessionalServicesModule } from './modules/professional-services/professional-services.module';
import { SportInfoService } from './services/sport-info.service';
import { DemographicInfoService } from './services/demographic-info.service';
import { NutritionPlanService } from './services/nutrition-plan.service';
import { NotificationService } from './services/notification.service';
import { FoodInfoService } from './services/food-info.service';
import { TranslateModule, TranslateLoader, TranslateService } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { AppComponent } from './app.component';
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, '../../assets/i18n/', '.json');
}
@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    SocketIoModule.forRoot(config),
    RouterModule.forRoot(routes),
    CreateServiceModule,
    CreateEventModule,
    CoreModule,
    AuthModule,
    AthleteModule,
    EventOrganizerModule,
    ProfessionalServicesModule,
    MaterialModule,
    SharedModule,
  ],
  providers: [{ provide: MAT_DATE_LOCALE, useFactory: (localeService: LocaleService) => localeService.locale$, deps: [LocaleService] }, 
  TrainingPlanService, CreateServiceService, CreateEventService, AuthService, SportInfoService, DemographicInfoService, NutritionPlanService, NotificationService, FoodInfoService],
  bootstrap: []
})
export class AppModule { }
