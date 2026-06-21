// src/app/core/guards/role.guard.ts
import { CanActivateFn, ActivatedRouteSnapshot, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const roleGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const user = auth.getUser();

  console.log('[roleGuard] user:', user);
  const expectedRole = route.data?.['role'];
  console.log('[roleGuard] expectedRole:', expectedRole);

  if (!user) {
    router.navigate(['/login']);
    return false;
  }

  // Normalize role strings: accept "ADMIN" or "ROLE_ADMIN" and case-insensitive
  const userRoleNormalized = (user.role ?? '').toString().toUpperCase();
  const expectedNormalized = (expectedRole ?? '').toString().toUpperCase();

  if (userRoleNormalized === expectedNormalized
      || userRoleNormalized === `ROLE_${expectedNormalized}`
      || (`ROLE_${userRoleNormalized}` === expectedNormalized)) {
    return true;
  }

  // Mauvais rôle → redirige vers dashboard
  router.navigate(['/dashboard']);
  return false;
};