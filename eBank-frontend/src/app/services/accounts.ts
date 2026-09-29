import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AccountDetails } from '../model/accounts.model';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class AccountService {
  constructor(private http: HttpClient) { }
  private backendHost = `${environment.backendHost}`

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
