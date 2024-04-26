import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePublishedListComponent } from './service-published-list.component';

describe('ServicePublishedListComponent', () => {
  let component: ServicePublishedListComponent;
  let fixture: ComponentFixture<ServicePublishedListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ServicePublishedListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ServicePublishedListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
