import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Customer } from '../model/customer.model';
import { JsonPipe } from '@angular/common';

@Component({
  selector: 'app-customer-accounts',
  imports: [JsonPipe],
  templateUrl: './customer-accounts.html',
  styleUrl: './customer-accounts.css',
})
export class CustomerAccounts implements OnInit {
  customerId!: string;
  customer!: Customer;
  constructor(private route: ActivatedRoute, private router: Router) {
    this.customer = this.router.getCurrentNavigation()?.extras as Customer;
  }
  ngOnInit(): void {
    this.customerId = this.route.snapshot.params['id'];
  }
}
