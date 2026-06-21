import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AboutComponent } from './front-office/template/about/about.component';
import { ServicesComponent } from './front-office/template/services/services.component';
import { FooterComponent } from './shared/footer/footer.component';
import { TemplateComponent } from './front-office/template/template.component';
import { HomeComponent } from './front-office/template/home/home.component';
import { DashboardComponent } from './front-office/dashboard/dashboard.component';
import { TrainerDashboardComponent } from './back-office/trainer-dashboard/trainer-dashboard.component';
import { AdminDashboardComponent } from './back-office/admin-dashboard/admin-dashboard.component';
import { SidebarComponent } from './shared/sidebar/sidebar.component';
import { CompleteProfileComponent } from './auth/complete-profile/complete-profile.component';
import { PublicLayoutComponent } from './layouts/public-layout/public-layout.component';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { UserLayoutComponent } from './layouts/user-layout/user-layout.component';
import { AuthGuard } from './core/guards/auth.guards';
import { roleGuard } from './core/guards/role.guard';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { SettingsComponent } from './front-office/settings/settings.component';
import { ProfileComponent } from './front-office/profile/profile.component';
import { SidebarSettingsComponent } from './shared/sidebar-settings/sidebar-settings.component';
import { ProfilePhotoComponent } from './front-office/profile-photo/profile-photo.component';
import { ChangePasswordComponent } from './front-office/change-password/change-password.component';
import { ListUserComponent } from './back-office/list-user/list-user.component';
import { UpdateCvComponent } from './front-office/update-cv/update-cv.component';
import { RequestListComponent } from './back-office/request-list/request-list.component';
import { NotificationComponent } from './front-office/notification/notification.component';

const routes: Routes = [
  // ===== PUBLIC PAGES =====

  
  {
    path: '',
    component: PublicLayoutComponent,
    children: [
      { path: '', component: HomeComponent },
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent }
    ]
  },
  // ===== USER DASHBOARD =====
  {
    path: 'user',
    component: UserLayoutComponent,
    children: [
      {path: 'sidesettings', component: SidebarSettingsComponent},
      { path: 'dashboard', component: DashboardComponent , canActivate: [AuthGuard] },
      {path: 'settings', component: SettingsComponent, canActivate: [AuthGuard] },
      {path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
      {path: 'photo', component: ProfilePhotoComponent , canActivate: [AuthGuard] },
      {path: 'change-password', component: ChangePasswordComponent , canActivate: [AuthGuard] },
      {path: 'update-cv', component: UpdateCvComponent , canActivate: [AuthGuard] },
      {path: 'notifications', component: NotificationComponent , canActivate: [AuthGuard] }
    ], 
    
  },
  {
  path: 'admin', component: AdminLayoutComponent,
  // si tu as un AdminLayoutComponent (recommandé), mets-le comme component ici :
  // component: AdminLayoutComponent,
  canActivate: [AuthGuard, roleGuard], // <-- ajoute roleGuard ici
  data: { role: 'ADMIN' },
  children: [
    { path: 'dashboard', component: AdminDashboardComponent } , 
    {path: 'list-user', component: ListUserComponent} , 
    {path: 'request-list', component: RequestListComponent}
  ]
}
  
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
