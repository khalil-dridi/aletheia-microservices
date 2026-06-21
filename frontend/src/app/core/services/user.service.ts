import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageUser, UpdateUserRequest, User } from '../models/user.model';

export interface AdminUserListParams {
  page?: number;
  size?: number;
  /** Server-side search (name, email) */
  q?: string;
  role?: '' | 'ADMIN' | 'LEARNER' | 'INSTRUCTOR';
  /** Spring Data sort, e.g. `nom,asc` or `createdAt,desc` */
  sort?: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  
  private API = environment.apiUrl + '/user';
  constructor(private http: HttpClient) {}

  getUsers(params: AdminUserListParams = {}): Observable<PageUser> {
    const page = params.page ?? 0;
    const size = params.size ?? 10;
    const sort = params.sort ?? 'nom,asc';

    let httpParams = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', sort);

    const q = params.q?.trim();
    if (q) {
      httpParams = httpParams.set('q', q);
    }
    const role = params.role?.trim();
    if (role) {
      httpParams = httpParams.set('role', role);
    }

    return this.http.get<PageUser>(`${this.API}/admin/users`, { params: httpParams });
  }

  getRole(): string | null {
    return localStorage.getItem('role');
  }

  isAdmin(): boolean {
    return this.getRole() === 'ADMIN';
  }

  isInstructor(): boolean {
    return this.getRole() === 'INSTRUCTOR';
  }

  isLearner(): boolean {
    return this.getRole() === 'LEARNER';
  }

  isLogged(): boolean {
    return !!localStorage.getItem('token');
  }
  


updateCurrentUser(data: UpdateUserRequest) {
  return this.http.put<User>(`${this.API}/me`, data);
}
getCurrentUser() {
  return this.http.get<User>(`${this.API}/me`);
}
changePassword(data: { oldPassword: string; newPassword: string }) {
  return this.http.put(`${this.API}/change-password`, data, { responseType: 'text' });
}
uploadPhoto(file: File): Observable<User> {
  const formData = new FormData();
  formData.append('file', file);

  return this.http.post<User>(`${this.API}/me/photo`, formData);
}
deleteUser(id: number): Observable<string> {
  return this.http.delete(`${this.API}/admin/users/${id}`, {
    responseType: 'text'
  });
}
toggleUserStatus(id: number): Observable<string> {
  return this.http.put(`${this.API}/admin/users/${id}/status`, {}, {
    responseType: 'text'
  });
}

}