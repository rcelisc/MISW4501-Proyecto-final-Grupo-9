import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TrainingPlanComponent } from './training-plan.component';

describe('PlanTrainingComponent', () => {
  let component: TrainingPlanComponent;
  let fixture: ComponentFixture<TrainingPlanComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrainingPlanComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(TrainingPlanComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
