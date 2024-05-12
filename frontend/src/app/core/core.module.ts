import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { WelcomePageComponent } from './components/welcome-page/welcome-page.component';
import { MaterialModule } from '../shared/material.module';
import { UnauthorizedComponent } from './components/unauthorized/unauthorized.component';

@NgModule({
  declarations: [WelcomePageComponent, UnauthorizedComponent],
  imports: [
    CommonModule,
    MaterialModule
  ],
  providers: [],
})
export class CoreModule {}
