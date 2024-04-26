import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SportInfoComponent } from './sport-info.component';

describe('SportInfoComponent', () => {
  let component: SportInfoComponent;
  let fixture: ComponentFixture<SportInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SportInfoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(SportInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
