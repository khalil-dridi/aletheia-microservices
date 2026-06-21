import { Component, OnInit, ViewChild } from '@angular/core';
import { UpdateUserRequest, User } from 'src/app/core/models/user.model';
import { UserService } from 'src/app/core/services/user.service';
import { ConfirmDialogComponent } from 'src/app/shared/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  nom = '';
  prenom = '';
  bio = '';

  @ViewChild(ConfirmDialogComponent) confirmDialog!: ConfirmDialogComponent;

  constructor(private userService: UserService) {}

  // 🔥 se lance quand la page s'ouvre
  ngOnInit() {
    this.loadUser();
  }

  loadUser() {
    this.userService.getCurrentUser().subscribe({
      next: (user: User) => {
        console.log("USER:", user);

        this.nom = user.nom;
        this.prenom = user.prenom;
        this.bio = user.bio || '';
      },
      error: (err) => {
        console.error("LOAD ERROR", err);
      }
    });
  }

  confirmAndSave() {
    this.confirmDialog.open({
      title: 'Update Profile',
      message: 'Are you sure you want to update your profile?',
      confirmText: 'Yes, Update',
      cancelText: 'Cancel'
    });
  }

  onConfirmDialogResult(confirmed: boolean) {
    if (confirmed) {
      this.saveProfile();
    }
  }

  saveProfile() {
    const payload: UpdateUserRequest = {
      nom: this.nom,
      prenom: this.prenom,
      bio: this.bio,
    };
    console.log(localStorage.getItem('token'));

    console.log("Sending:", payload);

    this.userService.updateCurrentUser(payload).subscribe({
      next: (res) => {
        console.log("SUCCESS", res);
        alert("Profile updated !");
      },
      error: (err) => {
        console.error("ERROR", err);
        alert("Error !");
      }
    });
  }
}