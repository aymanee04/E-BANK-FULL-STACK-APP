import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

export const authenticationGuard: CanActivateFn = (route, state) => {
  const authService = inject(Auth);
  const router = inject(Router)
  if (!authService.IsAuthenticated) {
    router.navigateByUrl('/login');
    return false;
  } else {
    return true;
  }
};
