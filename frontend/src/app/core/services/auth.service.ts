import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { tap } from 'rxjs';
import { User } from '../models/user.model';

interface LoginResponse {
  token: string;
  userId: number;
  email: string;
  role: string;
  nom: string;
  prenom: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private API = environment.apiUrl + '/auth';

  constructor(private http: HttpClient) {}

  login(data: { email: string; password: string }) {
    return this.http.post<LoginResponse>(`${this.API}/login`, data)
      .pipe(
        tap(res => {

          // 1️⃣ Stocker token
          localStorage.setItem('token', res.token);

          // 2️⃣ Construire objet User
          const user: User = {
            id: res.userId,
            email: res.email,
            role: res.role as any,
            nom: res.nom,
            prenom: res.prenom,
            enabled: true
          };

          // 3️⃣ Stocker user complet
          localStorage.setItem('user', JSON.stringify(user));
        })
      );
  }

  register(data: any) {
    return this.http.post(`${this.API}/register`, data);
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  getUser(): User | null {
    const data = localStorage.getItem('user');
    return data ? JSON.parse(data) : null;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}