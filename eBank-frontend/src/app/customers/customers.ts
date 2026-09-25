import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CustomerService } from '../services/customer';
import { Customer } from '../model/customer.model';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-customers',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customers.html',
  styleUrls: ['./customers.css'],
})
export class Customers implements OnInit {



  customers: Customer[] = [];
  errorMessage?: string;
  searchFormGroup!: FormGroup;

  constructor(private customerService: CustomerService, private fb: FormBuilder, private router: Router) { }

  ngOnInit(): void {
    this.searchFormGroup = this.fb.group({
      keyword: this.fb.control("")
    })
    this.customerService.getCustomers().subscribe({
      next: (data) => {
        this.customers = data;
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    });
  }
  handleSearchCustomers() {
    const kw = this.searchFormGroup.value.keyword ?? '';

    this.customerService.searchCustomers(kw).subscribe({
      next: (data: Customer[]) => {
        this.customers = data; // assign the actual array
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    });
  }
  handleDeleteCustomer(c: Customer) {
    let conf = confirm("Are you sure you want to delete this customer " + c.name + " ?");
    if (!conf) return;
    this.customerService.deleteCustomer(c.id).subscribe({
      next: (resp) => {
        this.handleSearchCustomers();
      },
      error: (err) => {
        this.errorMessage = err.message;
      }
    })
  }
  handleCustomerAccounts(customer: Customer) {
    this.router.navigateByUrl("/customer-accounts/" + customer.id, { state: customer });
  }
}