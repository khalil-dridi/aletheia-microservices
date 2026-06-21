import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/core/services/user.service';
import { User } from 'src/app/core/models/user.model';
import { ConfirmDialogComponent } from 'src/app/shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-profile-photo',
  templateUrl: './profile-photo.component.html',
  styleUrls: ['./profile-photo.component.css']
})
export class ProfilePhotoComponent implements OnInit {
  @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;

  currentUser: User | null = null;
  selectedFile: File | null = null;
  previewUrl: string | null = null;
  isUploading = false;
  uploadError: string | null = null;
  uploadSuccess = false;
  isDragOver = false;

  private readonly MAX_FILE_SIZE = 2 * 1024 * 1024; // 2MB
  private readonly ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCurrentUser();
  }

  private loadCurrentUser(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
      },
      error: (err) => {
        console.error('Failed to load current user', err);
      }
    });
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    event.stopPropagation();
    this.isDragOver = false;

    const files = event.dataTransfer?.files;
    if (files && files.length > 0) {
      this.handleFile(files[0]);
    }
  }

  private handleFile(file: File): void {
    this.uploadError = null;
    this.uploadSuccess = false;

    // Validate file type
    if (!this.ALLOWED_TYPES.includes(file.type)) {
      this.uploadError = 'Invalid file type. Please select JPEG, PNG, GIF, or WebP image.';
      return;
    }

    // Validate file size
    if (file.size > this.MAX_FILE_SIZE) {
      this.uploadError = 'File size exceeds 2MB limit.';
      return;
    }

    this.selectedFile = file;
    this.createPreview(file);
  }

  onFileSelected(event: Event): void {
    if (this.isUploading) {
      return; // Prevent file selection while uploading
    }

    this.uploadError = null;
    this.uploadSuccess = false;
    
    const input = event.target as HTMLInputElement;
    const files = input.files;

    if (!files || files.length === 0) {
      this.selectedFile = null;
      this.previewUrl = null;
      return;
    }

    this.handleFile(files[0]);
    this.resetFileInput(); // Reset after selection for consistency
  }

  private createPreview(file: File): void {
    const reader = new FileReader();
    
    reader.onload = () => {
      this.previewUrl = reader.result as string;
    };

    reader.onerror = () => {
      this.uploadError = 'Failed to read file.';
      this.selectedFile = null;
      this.previewUrl = null;
    };

    reader.readAsDataURL(file);
  }

  onUpload(): void {
    if (!this.selectedFile) {
      this.uploadError = 'Please select a file first.';
      return;
    }

    // Show confirmation dialog
    this.confirmDialog.open({
      title: 'Confirm Photo Upload',
      message: 'Are you sure you want to upload this profile photo?',
      confirmText: 'Upload',
      cancelText: 'Cancel'
    });
  }

  onDialogConfirmed(confirmed: boolean): void {
    if (confirmed) {
      this.onConfirmUpload();
    }
  }

  onConfirmUpload(): void {
    if (!this.selectedFile) {
      this.uploadError = 'Please select a file first.';
      return;
    }

    this.isUploading = true;
    this.uploadError = null;
    this.uploadSuccess = false;

    this.userService.uploadPhoto(this.selectedFile).subscribe({
      next: (user) => {
        this.currentUser = user;
        this.uploadSuccess = true;
        this.selectedFile = null;
        this.previewUrl = null;
        this.resetFileInput();

        // Auto-hide success message after 3 seconds
        setTimeout(() => {
          this.uploadSuccess = false;
        }, 3000);
      },
      error: (err) => {
        console.error('Photo upload failed', err);
        this.uploadError = err?.error?.message || 'Failed to upload photo. Please try again.';
      },
      complete: () => {
        this.isUploading = false;
      }
    });
  }

  onCancel(): void {
    this.selectedFile = null;
    this.previewUrl = null;
    this.uploadError = null;
    this.uploadSuccess = false;
    this.resetFileInput();
  }

  private resetFileInput(): void {
    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }

  isUploadDisabled(): boolean {
    return !this.selectedFile || this.isUploading;
  }

  getPhotoUrl(): string {
    return this.currentUser?.photoUrl || 'assets/default-avatar.png';
  }

  getUserAltText(): string {
    if (this.currentUser?.prenom && this.currentUser?.nom) {
      return `${this.currentUser.prenom} ${this.currentUser.nom}`;
    }
    return 'User profile photo';
  }
}
