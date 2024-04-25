import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { CreateServiceComponent } from './components/create-service/create-service.component';
import { ServiceListComponent } from './components/service-list/service-list.component';
import { MaterialModule } from '../../shared/material.module';

@NgModule({
  declarations: [
    CreateServiceComponent,
    ServiceListComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MaterialModule
  ],
  providers: []
})
export class CreateServiceModule { }
