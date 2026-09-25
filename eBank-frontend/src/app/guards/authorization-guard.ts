import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../services/auth';

export const authorizationGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router)
  if (auth.roles.includes('ADMIN')) {
    return true;
  } else {
    router.navigateByUrl('/admin/notAuthorized');
    return false;
  }
};
