import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanTrainingComponent } from './plan-training.component';

describe('PlanTrainingComponent', () => {
  let component: PlanTrainingComponent;
  let fixture: ComponentFixture<PlanTrainingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanTrainingComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PlanTrainingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
