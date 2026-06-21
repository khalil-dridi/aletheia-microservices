import { Component, HostListener, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';
import { InstructorRequest } from 'src/app/core/models/instructor-request.model';
import { InstructorRequestService } from 'src/app/core/services/instructor-request.service';
import { CvService } from 'src/app/core/services/cv.service';

@Component({
  selector: 'app-request-list',
  templateUrl: './request-list.component.html',
  styleUrls: ['./request-list.component.css']
})
export class RequestListComponent implements OnInit {
  requests: InstructorRequest[] = [];
  isLoading = false;
  actionLoadingId: number | null = null;
  successMessage = '';
  errorMessage = '';

  cvAnalysisOpen = false;
  cvAnalysisLoading = false;
  cvAnalysisResult = '';
  cvAnalysisError = '';
  cvAnalyzingRequestId: number | null = null;
  cvAnalysisSubjectName = '';
  private cvAnalysisSub?: Subscription;

  constructor(
    private instructorRequestService: InstructorRequestService,
    private cvService: CvService
  ) {}

  ngOnInit(): void {
    this.loadRequests();
  }

  @HostListener('document:keydown.escape')
  onEscapeCloseCvModal(): void {
    if (this.cvAnalysisOpen) {
      this.closeCvAnalysis();
    }
  }

  loadRequests(): void {
    this.clearMessages();
    this.isLoading = true;

    this.instructorRequestService
      .getAllRequests()
      .pipe(finalize(() => (this.isLoading = false)))
      .subscribe({
        next: (requests) => {
          this.requests = [...requests].sort(
            (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
          );
        },
        error: () => {
          this.errorMessage = 'Unable to load requests right now. Please try again.';
        }
      });
  }

  openCv(url: string): void {
    window.open(url, '_blank', 'noopener,noreferrer');
  }

  openCvAnalysis(request: InstructorRequest): void {
    if (!request.cvUrl?.trim()) {
      this.clearMessages();
      this.errorMessage = 'No CV file is attached to this request.';
      return;
    }

    this.cvAnalysisSub?.unsubscribe();
    this.cvAnalysisOpen = true;
    this.cvAnalysisLoading = true;
    this.cvAnalysisResult = '';
    this.cvAnalysisError = '';
    this.cvAnalyzingRequestId = request.id;
    this.cvAnalysisSubjectName = request.userName;

    this.cvAnalysisSub = this.cvService
      .analyzeCvFromUrl(request.cvUrl)
      .pipe(
        finalize(() => {
          this.cvAnalysisLoading = false;
          this.cvAnalyzingRequestId = null;
        })
      )
      .subscribe({
        next: (text) => {
          this.cvAnalysisResult = text;
        },
        error: () => {
          this.cvAnalysisError =
            'Unable to analyze this CV. The file may be unreachable or the analysis failed.';
        }
      });
  }

  closeCvAnalysis(): void {
    this.cvAnalysisSub?.unsubscribe();
    this.cvAnalysisSub = undefined;
    this.cvAnalysisOpen = false;
    this.cvAnalysisResult = '';
    this.cvAnalysisError = '';
    this.cvAnalysisLoading = false;
  }

  onCvModalBackdrop(event: MouseEvent): void {
    const el = event.target as HTMLElement;
    if (el.classList.contains('cv-modal-overlay')) {
      this.closeCvAnalysis();
    }
  }

  formatCreatedAt(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }
    return new Intl.DateTimeFormat('en-US', { year: 'numeric', month: 'short', day: 'numeric' }).format(date);
  }

  approveRequest(request: InstructorRequest): void {
    if (!this.canTakeAction(request)) {
      return;
    }

    const confirmed = window.confirm(`Approve request from ${request.userName}?`);
    if (!confirmed) {
      return;
    }

    this.processAction(request.id, 'approve');
  }

  rejectRequest(request: InstructorRequest): void {
    if (!this.canTakeAction(request)) {
      return;
    }

    const confirmed = window.confirm(`Reject request from ${request.userName}?`);
    if (!confirmed) {
      return;
    }

    this.processAction(request.id, 'reject');
  }

  canTakeAction(request: InstructorRequest): boolean {
    return request.status === 'PENDING' && this.actionLoadingId !== request.id;
  }

  trackByRequestId(_: number, request: InstructorRequest): number {
    return request.id;
  }

  private processAction(id: number, action: 'approve' | 'reject'): void {
    this.clearMessages();
    this.actionLoadingId = id;

    const request$ =
      action === 'approve'
        ? this.instructorRequestService.approveRequest(id)
        : this.instructorRequestService.rejectRequest(id);

    request$
      .pipe(finalize(() => (this.actionLoadingId = null)))
      .subscribe({
        next: () => {
          this.requests = this.requests.map((item) =>
            item.id === id
              ? { ...item, status: action === 'approve' ? 'APPROVED' : 'REJECTED' }
              : item
          );
          this.successMessage =
            action === 'approve'
              ? 'Request approved successfully.'
              : 'Request rejected successfully.';
        },
        error: () => {
          this.errorMessage = `Unable to ${action} this request. Please retry.`;
        }
      });
  }

  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}
