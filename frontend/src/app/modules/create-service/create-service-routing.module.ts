import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CreateServiceComponent } from './components/create-service/create-service.component';
import { ServiceListComponent } from './components/service-list/service-list.component';

const routes: Routes = [
  { path: 'create', component: CreateServiceComponent },
  { path: 'services', component: ServiceListComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CreateServiceRoutingModule { }
