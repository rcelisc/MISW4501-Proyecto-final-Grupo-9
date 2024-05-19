import { Component } from '@angular/core';
import { MaterialModule } from '../../../material.module';
import { Router } from '@angular/router';
import { TranslateService, TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-welcome-page',
  standalone: true,
  imports: [MaterialModule, TranslateModule],
  templateUrl: './welcome-page.component.html',
  styleUrls: ['./welcome-page.component.scss']
})
export class WelcomePageComponent {

  constructor(private router: Router, private translate: TranslateService) {
    this.translate.setDefaultLang('en');
  }
  
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  navigateToRegister(): void {
    this.router.navigate(['/register']);
  }
}
