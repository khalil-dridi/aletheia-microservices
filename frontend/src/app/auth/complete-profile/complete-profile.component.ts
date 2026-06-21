import { Component } from '@angular/core';
import { ThemeService } from '../../core/services/theme.service';

@Component({
  selector: 'app-complete-profile',
  templateUrl: './complete-profile.component.html',
  styleUrls: ['./complete-profile.component.css']
})
export class CompleteProfileComponent {
  // ThemeService is used only for template binding to react to navbar dark/light toggle
  constructor(public themeService: ThemeService) {}
}
