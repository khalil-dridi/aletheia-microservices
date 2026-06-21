import { TestBed } from '@angular/core/testing';

import { InstructorRequestService } from './instructor-request.service';

describe('InstructorRequestService', () => {
  let service: InstructorRequestService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(InstructorRequestService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
