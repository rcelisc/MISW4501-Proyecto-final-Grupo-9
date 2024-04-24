import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CreateServiceRoutingModule } from './create-service-routing.module';
import { CreateServiceComponent } from './components/create-service/create-service.component';
import { ServiceListComponent } from './components/service-list/service-list.component';
import { MaterialModule } from '../../material.module';

@NgModule({
  declarations: [
    CreateServiceComponent,
    ServiceListComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    CreateServiceRoutingModule,
    MaterialModule
  ],
  providers: []
})
export class CreateServiceModule { }
