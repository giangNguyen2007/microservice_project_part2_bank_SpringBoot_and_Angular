import {Component, computed, effect, input} from '@angular/core';
import {BankAccount} from '../../Model/BankAccount';
import {FormBuilder, ReactiveFormsModule} from '@angular/forms';
import {TransactionService} from '../../services/transaction.service';
import {Router} from '@angular/router';
import {IncomingTransactionService} from '../../services/incomingTransaction.service';
import {CurrencyPipe, DatePipe, NgIf, SlicePipe} from '@angular/common';
import {IncomingTransaction} from '../../Model/IncomingTransaction';
import {HttpErrorResponse} from '@angular/common/http';

@Component({
  selector: 'incoming-transaction-section',
  imports: [
    CurrencyPipe,
    DatePipe,
    NgIf,
    ReactiveFormsModule,
    SlicePipe
  ],
  templateUrl: './incoming-transaction-section.html',
  styleUrl: './incoming-transaction-section.css'
})
export class IncomingTransactionSection {

  // to be passed from parent component
  currentSelectedAccount = input<BankAccount | null>(null);

  incomingTransactionList = computed( () => this.incomingTransactionService.getTransactionList() ) ;



  myEffect = effect(
    () => {

      // refetch transactions for the currently selected account
      // when selected account changes => fetch transactions for new accounts
      if (this.currentSelectedAccount()) {
        this.incomingTransactionService.fetchIncomingTransaction(this.currentSelectedAccount()!.id);
      }
    }
  )

  constructor(private incomingTransactionService: IncomingTransactionService,
              private transactionService: TransactionService) {

  }

  onValidationClick( id : string): void {

    this.incomingTransactionService.sendValidationRequest(id).subscribe(
      {
        next: ( transactions => {
          alert('Transaction validated successfully');

          // refresh the transaction list
          if (this.currentSelectedAccount()) {
            this.incomingTransactionService.fetchIncomingTransaction(this.currentSelectedAccount()!.id);
          }

          // refetch transaction list to update balance
          this.transactionService.fetchTransaction(this.currentSelectedAccount()!.id);

        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;

          console.log('Status: ' + (errorResponse.status ));

          console.log('fetch transactions failed: ' + (errorResponse.error));

        })

      }
    );

  }



}
