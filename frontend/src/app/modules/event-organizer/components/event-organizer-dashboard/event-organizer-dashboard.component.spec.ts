import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { EventOrganizerDashboardComponent } from './event-organizer-dashboard.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { AuthService } from '../../../../services/auth.service';
import { of } from 'rxjs';

describe('EventOrganizerDashboardComponent', () => {
  let component: EventOrganizerDashboardComponent;
  let fixture: ComponentFixture<EventOrganizerDashboardComponent>;
  let mockAuthService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    mockAuthService = jasmine.createSpyObj('AuthService', ['decodeToken', 'getUserById']);
    
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule,
        TranslateModule.forRoot()
      ],
      declarations: [
        
      ],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        TranslateService
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(EventOrganizerDashboardComponent);
    component = fixture.componentInstance;

    // Mock user data response
    mockAuthService.decodeToken.and.returnValue({ user_id: 1 });
    mockAuthService.getUserById.and.returnValue(of({
      name: 'Test User',
      type: 'event_organizer'
    }));
  });

  beforeEach(() => {
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have dashboard items initialized correctly', () => {
    expect(component.dashboardItems.length).toBe(3);
    expect(component.dashboardItems[0].titleKey).toEqual('createEvents');
    expect(component.dashboardItems[0].link).toEqual('/create-event');
    expect(component.dashboardItems[1].titleKey).toEqual('viewAndPublishEvents');
    expect(component.dashboardItems[2].titleKey).toEqual('viewEventCalendar');
  });
});
