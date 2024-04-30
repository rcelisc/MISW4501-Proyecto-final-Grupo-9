import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { EventOrganizerDashboardComponent } from './event-organizer-dashboard.component';

describe('EventOrganizerDashboardComponent', () => {
  let component: EventOrganizerDashboardComponent;
  let fixture: ComponentFixture<EventOrganizerDashboardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule, EventOrganizerDashboardComponent
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EventOrganizerDashboardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have dashboard items initialized correctly', () => {
    expect(component.dashboardItems.length).toBe(3);
    expect(component.dashboardItems[0].title).toEqual('Crear Eventos');
    expect(component.dashboardItems[0].content).toContain('Crea eventos');
    expect(component.dashboardItems[0].link).toEqual('/create-event');
    expect(component.dashboardItems[1].title).toEqual('Ver y Publicar Eventos');
    expect(component.dashboardItems[2].title).toEqual('Ver Calendario de Eventos');
  });
});
