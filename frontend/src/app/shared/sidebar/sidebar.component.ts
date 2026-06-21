import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { ThemeService } from 'src/app/core/services/theme.service';
import { UserService } from 'src/app/core/services/user.service';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  user: any;

  constructor(
    private auth: AuthService,
    private userService: UserService,
    private router: Router,
    public themeService: ThemeService
  ) {}

  ngOnInit(): void {
    this.userService.getCurrentUser().subscribe({
      next: (res) => {
        this.user = res;
        console.log('USER =>', this.user);
      },
      error: (err) => {
        console.error('Erreur chargement user', err);
      }
    });
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}