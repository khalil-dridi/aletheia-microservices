import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommonModule } from '@angular/common';
import { of } from 'rxjs';

import { RequestListComponent } from './request-list.component';
import { InstructorRequestService } from 'src/app/core/services/instructor-request.service';
import { CvService } from 'src/app/core/services/cv.service';

describe('RequestListComponent', () => {
  let component: RequestListComponent;
  let fixture: ComponentFixture<RequestListComponent>;
  let instructorRequestServiceMock: jasmine.SpyObj<InstructorRequestService>;
  let cvServiceMock: jasmine.SpyObj<Pick<CvService, 'analyzeCvFromUrl'>>;

  beforeEach(() => {
    instructorRequestServiceMock = jasmine.createSpyObj<InstructorRequestService>(
      'InstructorRequestService',
      ['getAllRequests', 'approveRequest', 'rejectRequest']
    );
    instructorRequestServiceMock.getAllRequests.and.returnValue(of([]));

    cvServiceMock = jasmine.createSpyObj<Pick<CvService, 'analyzeCvFromUrl'>>('CvService', [
      'analyzeCvFromUrl'
    ]);
    cvServiceMock.analyzeCvFromUrl.and.returnValue(of('analysis text'));

    TestBed.configureTestingModule({
      declarations: [RequestListComponent],
      imports: [CommonModule],
      providers: [
        { provide: InstructorRequestService, useValue: instructorRequestServiceMock },
        { provide: CvService, useValue: cvServiceMock }
      ]
    });
    fixture = TestBed.createComponent(RequestListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load requests on init', () => {
    expect(instructorRequestServiceMock.getAllRequests).toHaveBeenCalled();
  });
});
