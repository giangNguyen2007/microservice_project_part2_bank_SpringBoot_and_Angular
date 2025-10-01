import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncomingTransactionSection } from './incoming-transaction-section';

describe('IncomingTransactionSection', () => {
  let component: IncomingTransactionSection;
  let fixture: ComponentFixture<IncomingTransactionSection>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IncomingTransactionSection]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IncomingTransactionSection);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
