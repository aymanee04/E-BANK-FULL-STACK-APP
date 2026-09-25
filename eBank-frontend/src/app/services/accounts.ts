import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDetails } from '../model/accounts.model';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private http: HttpClient) { }
  backendHost: string = "http://localhost:8080";
  public getAccount(accountId: string, page: number, size: number): Observable<AccountDetails> {
    return this.http.get<AccountDetails>(this.backendHost + '/account/' + accountId + '/pageOperations?page=' + page + '&size=' + size)
  }

  public debit(accountId: string, amount: number, description: string) {
    let data = { accountId: accountId, amount: amount, description: description }
    return this.http.post(this.backendHost + "/account/debit", data);
  }

  public credit(accountId: string, amount: number, description: string) {
    let data = { accountId: accountId, amount: amount, description: description }
    return this.http.post(this.backendHost + "/account/credit", data);
  }

  public transfer(accountSource: string, accountDestination: string, amount: number, description: string) {
    let data = { accountSource, accountDestination, amount, description }
    return this.http.post(this.backendHost + "/account/transfer", data);
  }
}
