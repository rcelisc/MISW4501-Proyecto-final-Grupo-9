/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { NutritionPlanService } from './nutrition-plan.service';

describe('Service: NutritionPlan', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [NutritionPlanService]
    });
  });

  it('should ...', inject([NutritionPlanService], (service: NutritionPlanService) => {
    expect(service).toBeTruthy();
  }));
});
