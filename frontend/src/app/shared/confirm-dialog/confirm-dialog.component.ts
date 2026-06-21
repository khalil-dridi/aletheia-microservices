import { Component, EventEmitter, Output } from '@angular/core';

export interface ConfirmDialogData {
  title?: string;
  message: string;
  confirmText?: string;
  cancelText?: string;
}

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent {
  @Output() confirmed = new EventEmitter<boolean>();

  visible = false;
  title = 'Confirmation';
  message = '';
  confirmText = 'Confirm';
  cancelText = 'Cancel';

  constructor() {}

  open(data: ConfirmDialogData) {
    this.title = data.title || 'Confirmation';
    this.message = data.message;
    this.confirmText = data.confirmText || 'Confirm';
    this.cancelText = data.cancelText || 'Cancel';
    this.visible = true;
  }

  close() {
    this.visible = false;
  }

  onConfirm() {
    this.confirmed.emit(true);
    this.close();
  }

  onCancel() {
    this.confirmed.emit(false);
    this.close();
  }

  onOverlayClick(event: Event) {
    // Close only if clicking the overlay, not the modal content
    if (event.target === event.currentTarget) {
      this.onCancel();
    }
  }
}