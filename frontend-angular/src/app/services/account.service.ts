// create a service FaceSnapService with the following properties and methods:
import {computed, Injectable, signal} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, tap} from 'rxjs';
import {User} from '../Model/User';
import {BankAccount} from '../Model/BankAccount';
import {AuthService} from './auth.service';
import {ConfigService} from './config.service';

interface LoginResponse {
  token: string;
  name: string;
  userId: string;
  role: string;
}


@Injectable({
  // this means that the service is available application-wide as a singleton
  providedIn: 'root'
})
export class AccountService {

  private apiUrl: string;


  private accounts = signal<BankAccount[] | null>(null) ;


  constructor(private http: HttpClient, private authService : AuthService, private configService : ConfigService) {
    this.apiUrl =  this.configService.apiUrl + '/account';
  }



  createAccount(accountType : "COMPTE_COURANT" | "COMPTE_EPARGNE"): Observable<BankAccount>  {


    if (!this.authService.getToken() || !this.authService.getUsers()) {
      return new Observable(observer => {
        observer.error(new Error('Authentication required: Token or user not found'));
      });
    }

    const createAccountDto = { userId : this.authService.getUsers()?.userId , accountType};

    return this.http.post<BankAccount>(this.apiUrl,  createAccountDto);

  }

  fetchAccounts(): void  {

    if (!this.authService.getToken() || !this.authService.getUsers() ) {

      return ;
    }

    this.http.get<BankAccount[]>(this.apiUrl).subscribe(
      {
        next: (accounts => {
          console.log('fetch accounts successful, number of accounts: ' + accounts.length);

          this.accounts.set(accounts);

        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;

          console.log('Status: ' + (errorResponse.status ));

          console.log('fetch accounts failed: ' + (errorResponse.error.message()));

        })

      }
    )

    return;

  }

  getAccounts() : BankAccount[] | null {
    return this.accounts();
  }


  logoutAccounts() :  void {
    this.accounts.set(null);

  }
}
