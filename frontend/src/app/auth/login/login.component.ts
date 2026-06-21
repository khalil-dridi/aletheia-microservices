import { Component, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements AfterViewInit {

  cardVisible = false; // Pour animation CSS
  email = '';          // Stocker email utilisateur
  password = '';       // Stocker password
  errorMsg = '';       // Message erreur affiché dans page
  loading = false;     // Loader pendant login

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  ngAfterViewInit(): void {
    requestAnimationFrame(() => this.cardVisible = true);
  }

  login(): void {

    this.errorMsg = ''; // reset message erreur

    if (!this.email || !this.password) {
      this.errorMsg = "Email et mot de passe requis";
      return;
    }

    this.loading = true; // afficher loader

    this.auth.login({
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {

  const user = this.auth.getUser();

  if (!user) {
    this.errorMsg = "Erreur récupération utilisateur";
    this.loading = false;
    return;
  }

  // Redirection selon rôle
  if (user.role === 'ADMIN') {
    this.router.navigate(['/admin/dashboard']);
  } else {
    this.router.navigate(['/user/dashboard']);
  }

},
      error: () => {
        this.errorMsg = "Email ou mot de passe incorrect";
        this.loading = false;
      }
    });
  }
}