
// create a service FaceSnapService with the following properties and methods:
import {computed, Injectable, signal} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, tap} from 'rxjs';
import {User} from '../Model/User';
import {BankAccount} from '../Model/BankAccount';
import {AuthService} from './auth.service';
import {BankTransaction} from '../Model/BankTransaction';
import {IncomingTransaction} from '../Model/IncomingTransaction';
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
export class IncomingTransactionService {

  // private apiUrl = 'http://localhost:8085/incoming-transaction';
  private apiUrl: string;


  private incomingTransactionList = signal<IncomingTransaction[] | null>(null) ;


  constructor(private http: HttpClient, private authService : AuthService, private configService : ConfigService) {
    this.apiUrl =  this.configService.apiUrl + '/incoming-transaction';

  }

  fetchIncomingTransaction( accountId : string): void  {

    if (!this.authService.getToken() || !this.authService.getUsers() ) {
      console.error('Authentication required: Token or user not found');

      return ;
    }

    // create a json object with email and password
    //{ params: { accountId: accountId } }

    this.http.get<IncomingTransaction[]>(this.apiUrl + "/by-account", { params : { accountId : accountId} } ).subscribe(
      {
        next: ( transactions => {
          console.log('fetch transactions successful, number of transactions: ' +  transactions.length);

          // date field is parsed as string, need to convert to Date object
          const validTransaction : IncomingTransaction[]  = transactions.map(
            (tx) => ({
              ...tx,
              createdAt: new Date(tx.createdAt)  // convert date string to Date object
            })
          );

          const sortedTransaction = validTransaction.sort((a, b) => b.createdAt!.getTime() - a.createdAt!.getTime());

          this.incomingTransactionList.set( sortedTransaction);

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

  // ts : IncomingTransaction to be validated
  sendValidationRequest( transactionId : string): Observable<IncomingTransaction>  {

    // if (!this.authService.getToken() || !this.authService.getUsers() ) {
    //   console.error('Authentication required: Token or user not found');
    //
    //   return  ;
    // }


    return this.http.put<IncomingTransaction>(this.apiUrl + "/validate", null, { params : { id : transactionId} } );


  }

  getTransactionList() : IncomingTransaction[] | null {
    return this.incomingTransactionList();
  }

  onLogout() :  void {
    this.incomingTransactionList.set(null);

  }
}
