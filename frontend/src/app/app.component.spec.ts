import { TestBed, async } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { AppComponent } from './app.component';
import { AuthService } from './services/auth.service';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { Router, Event } from '@angular/router';
import { Subject, of } from 'rxjs';

describe('AppComponent', () => {
  let mockAuthService: jasmine.SpyObj<AuthService>;
  let mockRouter: jasmine.SpyObj<Router>;
  let translateService: TranslateService;
  let routerEventsSubject: Subject<Event>;

  beforeEach(async(() => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken', 'logoutUser']);
    routerEventsSubject = new Subject<Event>();
    mockRouter = jasmine.createSpyObj('Router', ['navigate'], { events: routerEventsSubject.asObservable() });

    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot()
      ],
      declarations: [
        
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter },
        TranslateService
      ]
    }).compileComponents();

    translateService = TestBed.inject(TranslateService);
  }));

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it(`should have as title 'SportApp'`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app.title).toEqual('SportApp');
  });

  it('should navigate to the appropriate dashboard on logo click', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    const decodedToken = { role: 'athlete' };
    mockAuthService.decodeToken.and.returnValue(decodedToken);

    localStorage.setItem('token', 'mock-token');

    app.handleLogoClick();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/athlete-dashboard']);
  });

  it('should navigate to login if the token is invalid on logo click', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    mockAuthService.decodeToken.and.returnValue(null);

    localStorage.setItem('token', 'mock-token');

    app.handleLogoClick();
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/']);
  });

  it('should clear local storage and navigate to login on logout', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;

    const decodedToken = { user_id: 1 };
    mockAuthService.decodeToken.and.returnValue(decodedToken);
    mockAuthService.logoutUser.and.returnValue(of({}));

    localStorage.setItem('token', 'mock-token');

    app.logout();
    expect(mockAuthService.logoutUser).toHaveBeenCalledWith(1);
    expect(mockRouter.navigate).toHaveBeenCalledWith(['/login']);
    expect(localStorage.getItem('token')).toBeNull();
  });
});
