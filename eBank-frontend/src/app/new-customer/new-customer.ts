import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Customer } from '../model/customer.model';
import { CustomerService } from '../services/customer';

@Component({
  selector: 'app-new-customer',
  imports: [ReactiveFormsModule],
  templateUrl: './new-customer.html',
  styleUrl: './new-customer.css',
})
export class NewCustomer implements OnInit {
  newCustomerFormGroup!: FormGroup;
    errorMessage?: string;
  constructor(private fb:FormBuilder , private customerService:CustomerService){}

  ngOnInit(){
    this.newCustomerFormGroup = this.fb.group({
      name: this.fb.control(null, [Validators.required]),
      email: this.fb.control(null, [Validators.required, Validators.email])
    })
  }
  handleSaveCustomer() {
    let customer:Customer = this.newCustomerFormGroup.value;
    this.customerService.addCustomer(customer).subscribe({
      next:data =>{
        alert("Customer " + data.name + " saved successfully!")
        this.newCustomerFormGroup.reset();
      }, error: err =>{
        this.errorMessage = err.message;
      }
    })
    }
}
