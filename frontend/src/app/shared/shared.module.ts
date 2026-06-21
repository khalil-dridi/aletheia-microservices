import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { NavbarComponent } from './navbar/navbar.component';
import { FooterComponent } from './footer/footer.component';
import { CourseCardComponent } from './course-card/course-card.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { SidebarAdminComponent } from './sidebar-admin/sidebar-admin.component';
import { NavbarLoginComponent } from './navbar-login/navbar-login.component';
import { SidebarSettingsComponent } from './sidebar-settings/sidebar-settings.component';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';



@NgModule({
  declarations: [
    NavbarComponent,
    FooterComponent,
    CourseCardComponent,
    SidebarComponent,
    SidebarAdminComponent,
    NavbarLoginComponent,
    SidebarSettingsComponent,
    ConfirmDialogComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    ReactiveFormsModule
  ],
  exports: [
    NavbarComponent,
    FooterComponent,
    SidebarComponent,
    CourseCardComponent,
    ReactiveFormsModule,
    RouterModule,
    SidebarAdminComponent,
    ConfirmDialogComponent,
  ]
})
export class SharedModule { }
