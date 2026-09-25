import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Auth } from '../services/auth';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login implements OnInit {
  formLogin!: FormGroup;

  constructor(private fb: FormBuilder, private auth: Auth, private router: Router) { }

  ngOnInit(): void {
    this.formLogin = this.fb.group({
      username: this.fb.control(''),
      password: this.fb.control('')
    })
  }
  handleLogin() {
    let username = this.formLogin.value.username;
    let password = this.formLogin.value.password;
    this.auth.login(username, password).subscribe({
      next: (data) => {
        this.auth.LoadProfile(data);
        this.router.navigateByUrl("/admin");
      }, error: (err) => {
        console.log(err);
      }
    })
  }
}
