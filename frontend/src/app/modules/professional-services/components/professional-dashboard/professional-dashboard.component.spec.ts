import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProfessionalDashboardComponent } from './professional-dashboard.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('ProfessionalDashboardComponent', () => {
  let component: ProfessionalDashboardComponent;
  let fixture: ComponentFixture<ProfessionalDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule, ProfessionalDashboardComponent // Import RouterTestingModule to mock the router
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProfessionalDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have dashboard items initialized correctly', () => {
    expect(component.dashboardItems.length).toBe(4);
    expect(component.dashboardItems[0].title).toEqual('Crear Servicios');
    expect(component.dashboardItems[1].title).toEqual('Ver Servicios y Publicar Servicios');
    expect(component.dashboardItems[2].title).toEqual('Crear Planes de Entrenamiento');
    expect(component.dashboardItems[3].title).toEqual('Crear Planes de Nutricion');
  });
});
