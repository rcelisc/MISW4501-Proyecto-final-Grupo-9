import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AthleteCalendarComponent } from './athlete-calendar.component';

describe('AthleteCalendarComponent', () => {
  let component: AthleteCalendarComponent;
  let fixture: ComponentFixture<AthleteCalendarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AthleteCalendarComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AthleteCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
