import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private backendHost = `${environment.backendHost}`

  IsAuthenticated: boolean = false;
  roles: any;
  username: any;
  accessToken!: string;
  constructor(private http: HttpClient, private router: Router) { }

  public login(username: string, password: string) {
    let options = {
      headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' })
    }
    let params = new HttpParams().set("username", username).set("password", password);
    return this.http.post(this.backendHost+"/auth/login", params, options);
  }
  LoadProfile(data: any) {
    this.IsAuthenticated = true;
    this.accessToken = data['access-token'];
    let decodedJwt: any = jwtDecode(this.accessToken);
    this.username = decodedJwt.sub;
    this.roles = decodedJwt.scope;
    window.localStorage.setItem("access-token", this.accessToken);
  }
  LoadJwtTokenFromLocalStorage() {
    let token = window.localStorage.getItem("access-token");
    if (token) {
      this.LoadProfile({ 'access-token': token });
      this.router.navigateByUrl("/admin/customers");
    }
  }
  logout() {
    this.IsAuthenticated = false;
    this.accessToken = '';
    this.roles = undefined;
    this.username = undefined;
    window.localStorage.removeItem("access-token");
    this.router.navigateByUrl("/login");
  }
}
