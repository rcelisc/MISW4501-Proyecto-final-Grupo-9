import { TestBed, ComponentFixture } from '@angular/core/testing';
import { WelcomePageComponent } from './welcome-page.component';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';

describe('WelcomePageComponent', () => {
  let component: WelcomePageComponent;
  let fixture: ComponentFixture<WelcomePageComponent>;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([]),
        WelcomePageComponent 
      ],
      declarations: [  ]
    }).compileComponents();

    fixture = TestBed.createComponent(WelcomePageComponent);
    component = fixture.componentInstance;
    router = TestBed.get(Router); // Deprecated use TestBed.inject
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to login when navigateToLogin is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.navigateToLogin();
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should navigate to register when navigateToRegister is called', () => {
    const navigateSpy = spyOn(router, 'navigate');
    component.navigateToRegister();
    expect(navigateSpy).toHaveBeenCalledWith(['/register']);
  });
});
