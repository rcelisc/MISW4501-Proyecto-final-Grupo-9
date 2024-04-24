import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { SocketIoModule } from 'ngx-socket-io';
import { config } from './socket.config';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TrainingPlanService } from './services/training-plan.service';
import { CrearPlanAlimentacionComponent } from './modules/crear-plan-alimentacion/crear-plan-alimentacion.component';
import { InicioDeportistaComponent } from './modules/inicio-deportista/inicio-deportista.component';
import { CreateServiceModule } from './modules/create-service/create-service.module';
import { MaterialModule } from './material.module';
import { RouterModule } from '@angular/router';
import { routes } from './app.routes';
import { CreateServiceService } from './services/create-service.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';




@NgModule({
  declarations: [			
      CrearPlanAlimentacionComponent,
      InicioDeportistaComponent
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
    MaterialModule
  ],
  providers: [TrainingPlanService, CreateServiceService],
  bootstrap: []
})
export class AppModule { }
