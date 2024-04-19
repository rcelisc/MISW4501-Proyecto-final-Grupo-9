import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SocketIoModule } from 'ngx-socket-io';
import { config } from './socket.config';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { TrainingPlanService } from './services/training-plan.service';
import { CrearPlanAlimentacionComponent } from './modules/crear-plan-alimentacion/crear-plan-alimentacion.component';
import { InicioDeportistaComponent } from './modules/inicio-deportista/inicio-deportista.component';
import { SocketService } from './services/socket.service';


@NgModule({
  declarations: [			
      CrearPlanAlimentacionComponent,
      InicioDeportistaComponent
   ],
  imports: [
    CommonModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    SocketIoModule.forRoot(config)
  ],
  providers: [TrainingPlanService],
})
export class AppModule { }
