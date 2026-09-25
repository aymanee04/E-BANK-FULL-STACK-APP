import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Auth } from '../services/auth';
import { catchError } from 'rxjs';

export const appHttpInterceptor: HttpInterceptorFn = (req, next) => {

  if (!req.url.includes('/auth/login')) {
    const authService = inject(Auth); // Use `inject` to get the AuthService instance

    const clonedRequest = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${authService.accessToken}`),
    });

    return next(clonedRequest).pipe(
      // if 401 logout user
      catchError(err => {
        if (err.status === 401) {
          authService.logout();
        }
        throw err;
      })
    );
  } else {
    return next(req);
  }
};
