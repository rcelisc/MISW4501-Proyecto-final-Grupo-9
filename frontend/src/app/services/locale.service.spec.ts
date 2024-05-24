import { TestBed } from '@angular/core/testing';
import { LocaleService } from './locale.service';

describe('LocaleService', () => {
  let service: LocaleService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [LocaleService]
    });
    service = TestBed.inject(LocaleService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize localeSubject with default value', () => {
    service.locale$.subscribe(locale => {
      expect(locale).toBe('en-US'); // Check if the default locale is set correctly
    });
  });

  it('should set a new locale', () => {
    const newLocale = 'fr-FR';

    service.setLocale(newLocale);

    service.locale$.subscribe(locale => {
      expect(locale).toBe(newLocale); // Check if the locale is updated correctly
    });
  });
});
