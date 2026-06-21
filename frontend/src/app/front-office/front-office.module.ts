import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CourseDetailsComponent } from './course-details/course-details.component';
import { ProfileComponent } from './profile/profile.component';
import { SharedModule } from '../shared/shared.module';
import { AboutComponent } from './template/about/about.component';
import { ServicesComponent } from './template/services/services.component';
import { TemplateComponent } from './template/template.component';
import { HomeComponent } from './template/home/home.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { SettingsComponent } from './settings/settings.component';
import { ProfilePhotoComponent } from './profile-photo/profile-photo.component';
import { ChangePasswordComponent } from './change-password/change-password.component';
import { FormsModule } from '@angular/forms';
import { UpdateCvComponent } from './update-cv/update-cv.component';
import { NotificationComponent } from './notification/notification.component';




@NgModule({
  declarations: [
    HomeComponent,
    CourseDetailsComponent,
    ProfileComponent,
    AboutComponent,
    ServicesComponent,
    TemplateComponent,
    DashboardComponent,
    SettingsComponent,
    ProfilePhotoComponent,
    ChangePasswordComponent,
    UpdateCvComponent,
    NotificationComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    FormsModule   
  ],
  exports: [
    HomeComponent,
    CourseDetailsComponent,
    ProfileComponent,
    AboutComponent,
    ServicesComponent
  ]
})
export class FrontOfficeModule { }
