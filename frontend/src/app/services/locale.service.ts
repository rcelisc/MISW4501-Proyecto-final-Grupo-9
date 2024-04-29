import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LocaleService {
  private localeSubject = new BehaviorSubject<string>('en-US');
  locale$ = this.localeSubject.asObservable();

  constructor() { }

  setLocale(newLocale: string) {
    this.localeSubject.next(newLocale);
  }
}
