import { Component } from '@angular/core';
import { finalize } from 'rxjs/operators';
import { InstructorRequestService } from 'src/app/core/services/instructor-request.service';

@Component({
  selector: 'app-update-cv',
  templateUrl: './update-cv.component.html',
  styleUrls: ['./update-cv.component.css']
})
export class UploadCvComponent {
  selectedFile: File | null = null;
  motivation = '';
  isDragging = false;
  isSubmitting = false;
  successMessage = '';
  errorMessage = '';

  constructor(private instructorRequestService: InstructorRequestService) {}

  onFileInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.applySelectedFile(file);
    input.value = '';
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging = false;
    const file = event.dataTransfer?.files?.[0] ?? null;
    this.applySelectedFile(file);
  }

  onMotivationChange(event: Event): void {
    const value = (event.target as HTMLTextAreaElement).value;
    this.motivation = value;
  }

  removeFile(): void {
    this.selectedFile = null;
    this.errorMessage = '';
  }

  submitRequest(): void {
    if (!this.selectedFile || this.isSubmitting) {
      return;
    }

    this.resetMessages();
    this.isSubmitting = true;

    this.instructorRequestService
      .createRequest(this.selectedFile, this.motivation)
      .pipe(finalize(() => (this.isSubmitting = false)))
      .subscribe({
        next: () => {
          this.successMessage = 'Your CV has been uploaded successfully. We will review your request shortly.';
          this.selectedFile = null;
          this.motivation = '';
        },
        error: (error) => {
          this.errorMessage =
            error?.error?.message ||
            'Upload failed. Please try again in a few moments.';
        }
      });
  }

  formatFileSize(file: File): string {
    const bytes = file.size;
    if (bytes < 1024) {
      return `${bytes} B`;
    }
    if (bytes < 1024 * 1024) {
      return `${(bytes / 1024).toFixed(1)} KB`;
    }
    return `${(bytes / (1024 * 1024)).toFixed(2)} MB`;
  }

  private applySelectedFile(file: File | null): void {
    this.resetMessages();

    if (!file) {
      return;
    }

    if (!this.isPdf(file)) {
      this.selectedFile = null;
      this.errorMessage = 'Only PDF files are allowed.';
      return;
    }

    this.selectedFile = file;
  }

  private isPdf(file: File): boolean {
    const isPdfType = file.type === 'application/pdf';
    const hasPdfExtension = file.name.toLowerCase().endsWith('.pdf');
    return isPdfType || hasPdfExtension;
  }

  private resetMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }
}

export { UploadCvComponent as UpdateCvComponent };
