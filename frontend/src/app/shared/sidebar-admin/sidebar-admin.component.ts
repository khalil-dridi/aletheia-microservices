import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/core/services/auth.service';
import { ThemeService } from 'src/app/core/services/theme.service';

@Component({
  selector: 'app-sidebar-admin',
  templateUrl: './sidebar-admin.component.html',
  styleUrls: ['./sidebar-admin.component.css']
})
export class SidebarAdminComponent {

  user = this.auth.getUser();

  constructor(
    private auth: AuthService,
    private router: Router,
    public themeService: ThemeService
  ) {}

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

}