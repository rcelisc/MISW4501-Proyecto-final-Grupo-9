import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WelcomePageComponent } from './components/welcome-page/welcome-page.component';
import { MaterialModule } from '../shared/material.module';

@NgModule({
  declarations: [WelcomePageComponent],
  imports: [
    CommonModule,
    MaterialModule
  ],
  providers: [],
})
export class CoreModule {}
