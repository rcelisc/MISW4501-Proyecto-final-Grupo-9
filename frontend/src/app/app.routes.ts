import { Routes } from '@angular/router';
import { HomeComponent } from './modules/home/home.component';
import { TipousuarioComponent } from './modules/tipousuario/tipousuario.component';
import { InicioProfesionalComponent } from './modules/inicioProfesional/inicioProfesional.component';
import { PlantrainingComponent } from './modules/plantraining/plantraining.component';
import { HistorialtrainingComponent } from './modules/historialtraining/historialtraining.component';
import { CreareventoComponent } from './modules/crearevento/crearevento.component';
import { InicioOrganizadorComponent } from './modules/inicioOrganizador/inicioOrganizador.component';
import { CrearPlanAlimentacionComponent } from './modules/crear-plan-alimentacion/crear-plan-alimentacion.component';
import { InicioDeportistaComponent } from './modules/inicio-deportista/inicio-deportista.component';
import { RegistrarUsuarioComponent } from './modules/registrar-usuario/registrar-usuario.component';
import { CrearServicioComponent } from './modules/crear-servicio/crear-servicio.component';
import { LoginComponent } from './modules/login/login.component';
import { MiCuentaComponent } from './modules/mi-cuenta/mi-cuenta.component';
import { ListarUsuariosProfesionalComponent } from './modules/listar-usuarios-profesional/listar-usuarios-profesional.component';

export const routes: Routes = [
  {path: '', component:HomeComponent},
  {path: 'home', component:HomeComponent},
  {path: 'tipousuario', component:TipousuarioComponent},
  {path: 'inicioprofesional', component:InicioProfesionalComponent},
  {path: 'planentrenamiento', component: PlantrainingComponent},
  {path: 'historialentrenamiento', component: HistorialtrainingComponent},
  {path: 'crearevento', component: CreareventoComponent},
  {path: 'inicioorganizador', component: InicioOrganizadorComponent},
  {path: 'crearplanalimentacion', component: CrearPlanAlimentacionComponent},
  {path: 'iniciodeportista', component: InicioDeportistaComponent},
  {path: 'registrarusuario', component: RegistrarUsuarioComponent},
  {path: 'crearservicio', component: CrearServicioComponent},
  {path: 'login', component: LoginComponent},
  {path: 'micuenta', component: MiCuentaComponent},
  {path: 'listausuriosprofesional', component: ListarUsuariosProfesionalComponent}

];
