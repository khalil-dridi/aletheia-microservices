import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler } from '@angular/common/http';
import { AuthService } from '../services/auth.service'; // Service auth

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private auth: AuthService) {} // Injection service

  intercept(req: HttpRequest<any>, next: HttpHandler) {

    const token = this.auth.getToken(); // Lire token

    if (token) {
      // Cloner requête et ajouter header Authorization
      req = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(req); // Continuer requête
  }
}