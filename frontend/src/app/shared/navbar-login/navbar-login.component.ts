import { Component, ElementRef, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { ThemeService } from 'src/app/core/services/theme.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar-login',
  templateUrl: './navbar-login.component.html',
  styleUrls: ['./navbar-login.component.css']
})
export class NavbarLoginComponent {
 @ViewChild('searchInput') searchInput!: ElementRef<HTMLInputElement>;
  @ViewChild('userDropdown') userDropdown!: ElementRef<HTMLDivElement>;

  isLoggedIn = false; // This should be connected to your auth service
  isMobileMenuOpen = false;
  isUserDropdownOpen = false;
  searchControl = new FormControl('');
  searchQuery = '';
  currentRoute = '';

  // Mock user data - replace with actual user service
  currentUser = {
    name: 'John Doe',
    email: 'john@example.com',
    avatar: 'https://i.pravatar.cc/150?img=12'
  };

  menuItems = [
    { label: 'Home', route: '/', icon: '🏠' },
    { label: 'About', route: '/about', icon: 'ℹ️' },
    { label: 'Services', route: '/services', icon: '⚙️' },
    { label: 'Contact', route: '/contact', icon: '📧' }
  ];

  constructor(
    public router: Router,
    private elementRef: ElementRef,
    public themeService: ThemeService
  ) {}

  ngOnInit(): void {
    // Initialize current route
    this.currentRoute = this.router.url;
    
    // Subscribe to route changes to update active state
    this.router.events.subscribe(() => {
      this.currentRoute = this.router.url;
    });


  }



  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  toggleUserDropdown(): void {
    this.isUserDropdownOpen = !this.isUserDropdownOpen;
  }

  navigateTo(route: string): void {
    this.router.navigate([route]);
    this.isMobileMenuOpen = false;
  }


  navigateToRegister(): void {
    this.router.navigate(['/auth/register']);
  }

  onSearchSubmit(): void {
    if (this.searchQuery.trim()) {
      this.router.navigate(['/courses'], { queryParams: { search: this.searchQuery } });
      this.isMobileMenuOpen = false;
    }
  }

  onProfileClick(): void {
    this.router.navigate(['/profile']);
    this.isUserDropdownOpen = false;
  }

  onMyCoursesClick(): void {
    this.router.navigate(['/my-courses']);
    this.isUserDropdownOpen = false;
  }

  onLogout(): void {
    // Implement logout logic
    this.isLoggedIn = false;
    this.isUserDropdownOpen = false;
    this.router.navigate(['/home']);
  }

  // Mock login - replace with actual auth service
  mockLogin(): void {
    this.isLoggedIn = true;
  }

  
}
