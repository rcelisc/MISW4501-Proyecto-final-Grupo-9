import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { SocketIoModule } from 'ngx-socket-io';
import { config } from './socket.config';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TrainingPlanService } from './services/training-plan.service';
import { CrearPlanAlimentacionComponent } from './modules/crear-plan-alimentacion/crear-plan-alimentacion.component';
import { CreateServiceModule } from './modules/create-service/create-service.module';
import { MaterialModule } from './shared/material.module';
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




@NgModule({
  declarations: [			
      CrearPlanAlimentacionComponent,
   ],
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
    MaterialModule
  ],
  providers: [{ provide: MAT_DATE_LOCALE, useFactory: (localeService: LocaleService) => localeService.locale$, deps: [LocaleService] },
  TrainingPlanService, CreateServiceService, CreateEventService, AuthService, SportInfoService, DemographicInfoService],
  bootstrap: []
})
export class AppModule { }
