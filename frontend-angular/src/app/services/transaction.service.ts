
// create a service FaceSnapService with the following properties and methods:
import {computed, Injectable, signal} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, tap} from 'rxjs';
import {User} from '../Model/User';
import {BankAccount} from '../Model/BankAccount';
import {AuthService} from './auth.service';
import {BankTransaction} from '../Model/BankTransaction';
import {ConfigService} from './config.service';

interface CreateTransactionDto {
  accountId: string;
  amount: number;
  description: string;
}


@Injectable({
  // this means that the service is available application-wide as a singleton
  providedIn: 'root'
})
export class TransactionService {

  // private apiUrl = 'http://localhost:8085/transaction';

  private apiUrl: string;


  private transactionList = signal<BankTransaction[] | null>(null) ;


  constructor(private http: HttpClient, private authService : AuthService, private configService : ConfigService) {
    this.apiUrl =  this.configService.apiUrl + '/transaction';

  }

  createDepositTransaction( accountId : string, amount : number, description: string): Observable<any>  {

    console.log( 'createTransaction called with accountId=' + accountId + ', amount=' + amount + ', description=' + description);

    if (!accountId || amount == 0 || !description) {
      return new Observable(observer => {
        observer.error(new Error('Invalid input: accountId, amount and description are required'));
      });
    }

    if (!this.authService.getToken() || !this.authService.getUsers()) {
      return new Observable(observer => {
        observer.error(new Error('Authentication required: Token or user not found'));
      });
    }

    const createTransactionDto = { accountId, amount, description};
    // create a json object with email and password

    return this.http.post<any>(this.apiUrl + "/deposit",  createTransactionDto);

  }



  createInternalTransferTransaction(debitAccountId : string, creditAccountId: string, amount : number, description: string): Observable<any>  {

    console.log( 'createTransaction called with accountId=' + debitAccountId + ', amount=' + amount + ', description=' + description);

    if (!debitAccountId || amount == 0 || !description) {
      return new Observable(observer => {
        observer.error(new Error('Invalid input: accountId, amount and description are required'));
      });
    }

    if (!this.authService.getToken() || !this.authService.getUsers()) {
      return new Observable(observer => {
        observer.error(new Error('Authentication required: Token or user not found'));
      });
    }

    const createTransactionDto = { accountId: debitAccountId, destinationAccountId: creditAccountId, amount, description, isExternalTransaction: false};
    // create a json object with email and password

    // backend return empty body with status 201 is success => use any
    return this.http.post<any>(this.apiUrl + "/internal-transfer",  createTransactionDto);

  }

  fetchTransaction( accountId : string): void  {


    if (!this.authService.getToken() || !this.authService.getUsers() ) {
      console.error('Authentication required: Token or user not found');

      return ;
    }

    // create a json object with email and password
    // { params: { accountId: accountId } }

    // the resulted url is like http://localhost:8085/transaction?accountId=12345
    this.http.get<BankTransaction[]>(this.apiUrl + "/by-account", { params : { accountId : accountId} } ).subscribe(
      {
        next: ( transactions => {
          console.log('fetch transactions successful, number of transactions: ' +  transactions.length);

          // date field is parsed as string, need to convert to Date object
          const validTransaction : BankTransaction[]  = transactions.map(
            (tx) => ({
              ...tx,
              createdAt: new Date(tx.createdAt)  // convert date string to Date object
            })
          );

          const sortedTransaction = validTransaction.sort((a, b) => b.createdAt!.getTime() - a.createdAt!.getTime());

          this.transactionList.set( sortedTransaction);

        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;

          console.log('Status: ' + (errorResponse.status ));

          console.log('fetch transactions failed: ' + (errorResponse.error.message));

        })

      }
    )

    return;

  }

  getTransactionList() : BankTransaction[] | null {
    return this.transactionList();
  }

  onLogout() :  void {
    this.transactionList.set(null);

  }

}
