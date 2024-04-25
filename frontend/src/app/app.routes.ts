
import { Routes } from '@angular/router';
import { HomeComponent } from './modules/home/home.component';
import { TipousuarioComponent } from './modules/tipousuario/tipousuario.component';
import { InicioProfesionalComponent } from './modules/inicioProfesional/inicioProfesional.component';
import { PlantrainingComponent } from './modules/plantraining/plantraining.component';
import { HistorialtrainingComponent } from './modules/historialtraining/historialtraining.component';
import { InicioOrganizadorComponent } from './modules/inicioOrganizador/inicioOrganizador.component';
import { CrearPlanAlimentacionComponent } from './modules/crear-plan-alimentacion/crear-plan-alimentacion.component';
import { InicioDeportistaComponent } from './modules/inicio-deportista/inicio-deportista.component';
import { RegistrarUsuarioComponent } from './modules/registrar-usuario/registrar-usuario.component';
import { LoginComponent } from './modules/login/login.component';
import { MiCuentaComponent } from './modules/mi-cuenta/mi-cuenta.component';
import { ListarUsuariosProfesionalComponent } from './modules/listar-usuarios-profesional/listar-usuarios-profesional.component';
import { ServiceListComponent } from './modules/create-service/components/service-list/service-list.component';
import { ProfessionalProfileComponent } from './modules/professional-profile/professional-profile.component';
import { CreateServiceComponent } from './modules/create-service/components/create-service/create-service.component';
import { CreateEventComponent } from './modules/create-event/components/create-event/create-event.component';
import { EventListComponent } from './modules/create-event/components/event-list/event-list.component';

export const routes: Routes = [
  {path: '', component:HomeComponent},
  {path: 'home', component:HomeComponent},
  {path: 'tipousuario', component:TipousuarioComponent},
  {path: 'inicioprofesional', component:InicioProfesionalComponent},
  {path: 'planentrenamiento', component: PlantrainingComponent},
  {path: 'historialentrenamiento', component: HistorialtrainingComponent},
  {path: 'inicioorganizador', component: InicioOrganizadorComponent},
  {path: 'crearplanalimentacion', component: CrearPlanAlimentacionComponent},
  {path: 'iniciodeportista', component: InicioDeportistaComponent},
  {path: 'registrarusuario', component: RegistrarUsuarioComponent},
  {path: 'login', component: LoginComponent},
  {path: 'micuenta', component: MiCuentaComponent},
  {path: 'listausuriosprofesional', component: ListarUsuariosProfesionalComponent},
  {path: 'create-service', component: CreateServiceComponent},
  {path: 'service-list', component: ServiceListComponent},
  {path: 'create-event', component: CreateEventComponent},
  {path: 'event-list', component: EventListComponent},
  {path: 'professional-profile', component: ProfessionalProfileComponent},
];
