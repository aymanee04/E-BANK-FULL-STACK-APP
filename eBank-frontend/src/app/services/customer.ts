import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Customer } from '../model/customer.model';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  constructor(private http:HttpClient){}
  backendHost: string = "http://localhost:8080";
  public getCustomers(): Observable<Customer[]>{
    return this.http.get<Customer[]>(this.backendHost + "/customers")
  }
  public searchCustomers(keyword: string): Observable<Customer[]>{
    return this.http.get<Customer[]>(this.backendHost + "/customers/search?keyword=" + keyword)
  }
  public addCustomer(customer: Customer): Observable<Customer>{
    return this.http.post<Customer>(this.backendHost + "/customers", customer)
  }
  public deleteCustomer(id:number): Observable<void>{
    return this.http.delete<void>(this.backendHost + "/customers/" + id)
  }
}
