import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { ManageCoursesComponent } from './manage-courses/manage-courses.component';
import { TrainerDashboardComponent } from './trainer-dashboard/trainer-dashboard.component';
import { ListUserComponent } from './list-user/list-user.component';
import { RequestListComponent } from './request-list/request-list.component';



@NgModule({
  declarations: [
    AdminDashboardComponent,
    ManageCoursesComponent,
    TrainerDashboardComponent,
    ListUserComponent,
    RequestListComponent
  ],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class BackOfficeModule { }
