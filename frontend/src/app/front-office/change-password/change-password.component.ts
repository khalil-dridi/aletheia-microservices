import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from 'src/app/core/services/user.service';
import { ConfirmDialogComponent } from 'src/app/shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.css']
})
export class ChangePasswordComponent implements OnInit {

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';

  showCurrentPassword = false;
  showNewPassword = false;
  showConfirmPassword = false;

  passwordStrength = 0;
  hasMinLength = false;
  hasUpperCase = false;
  hasLowerCase = false;
  hasNumber = false;

  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;

  constructor(
    private router: Router,
    private userService: UserService
  ) {}

  ngOnInit() {}

  toggleCurrentPasswordVisibility() {
    this.showCurrentPassword = !this.showCurrentPassword;
  }

  toggleNewPasswordVisibility() {
    this.showNewPassword = !this.showNewPassword;
  }

  toggleConfirmPasswordVisibility() {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  checkPasswordStrength() {
    const password = this.newPassword;

    this.hasMinLength = password.length >= 8;
    this.hasUpperCase = /[A-Z]/.test(password);
    this.hasLowerCase = /[a-z]/.test(password);
    this.hasNumber = /\d/.test(password);

    const checks = [
      this.hasMinLength,
      this.hasUpperCase,
      this.hasLowerCase,
      this.hasNumber
    ];

    const passedChecks = checks.filter(check => check).length;
    this.passwordStrength = (passedChecks / checks.length) * 100;
  }

  getStrengthText(): string {
    if (this.passwordStrength === 0) return 'Very Weak';
    if (this.passwordStrength <= 25) return 'Weak';
    if (this.passwordStrength <= 50) return 'Fair';
    if (this.passwordStrength <= 75) return 'Good';
    return 'Strong';
  }

  isFormValid(): boolean {
    return !!(
      this.currentPassword.trim() &&
      this.newPassword.trim() &&
      this.confirmPassword.trim() &&
      this.newPassword === this.confirmPassword &&
      this.passwordStrength >= 75
    );
  }

  onSubmit() {
    if (!this.isFormValid()) {
      return;
    }

    this.confirmDialog.open({
      title: 'Confirm Password Change',
      message: 'Are you sure you want to change your password?',
      confirmText: 'Yes, Change Password',
      cancelText: 'Cancel'
    });
  }

  onConfirmDialogResult(confirmed: boolean) {
    if (confirmed) {
      this.executeChangePassword();
    }
  }

  private executeChangePassword() {
    const payload = {
      oldPassword: this.currentPassword,
      newPassword: this.newPassword
    };

    this.userService.changePassword(payload).subscribe({
      next: () => {
        alert('Password changed successfully!');
        this.router.navigate(['/user/sidesettings']);
      },
      error: (err) => {
        console.error(err);
        alert('Error updating password');
      }
    });
  }
}