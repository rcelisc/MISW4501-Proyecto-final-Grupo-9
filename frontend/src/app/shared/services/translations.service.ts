import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

@Injectable({
  providedIn: 'root'
})
export class TranslationService {

  constructor(private translate: TranslateService) {
    // Set default language
    this.translate.setDefaultLang('en');
    this.translate.use('en'); // Use default language
  }

  switchLanguage(language: string) {
    this.translate.use(language);
  }

  get currentLang() {
    return this.translate.currentLang;
  }

  instant(key: string): string {
    return this.translate.instant(key);
  }
}
