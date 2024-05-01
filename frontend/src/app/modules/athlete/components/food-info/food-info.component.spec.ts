import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FoodInfoComponent } from './food-info.component';

describe('FoodInfoComponent', () => {
  let component: FoodInfoComponent;
  let fixture: ComponentFixture<FoodInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FoodInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(FoodInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
