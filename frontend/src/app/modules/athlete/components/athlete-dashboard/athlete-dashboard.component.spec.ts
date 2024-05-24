import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AthleteDashboardComponent } from './athlete-dashboard.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('AthleteDashboardComponent', () => {
  let component: AthleteDashboardComponent;
  let fixture: ComponentFixture<AthleteDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule, // Handles routing-related directives and services
        AthleteDashboardComponent // Import the standalone component here
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AthleteDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have dashboard items defined', () => {
    expect(component.dashboardItems.length).toBeGreaterThan(0);
  });

  it('should display titles of dashboard items in the template', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    component.dashboardItems.forEach(item => {
      expect(compiled.textContent).toContain(item.title);
    });
  });
});
