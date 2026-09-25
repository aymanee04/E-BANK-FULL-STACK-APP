import { Component, OnInit, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Auth } from './services/auth';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit {
  protected readonly title = signal('eBank-frontend');
  constructor(private auth: Auth) { }
  ngOnInit(): void {
    this.auth.LoadJwtTokenFromLocalStorage();
  }
}
