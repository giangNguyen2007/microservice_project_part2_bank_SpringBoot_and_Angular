import {Component, computed, effect, EventEmitter, inject, input, Input, OnInit, Output} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {map, Observable} from 'rxjs';
import {User} from '../../Model/User';
import {AsyncPipe, CurrencyPipe, DatePipe, NgIf, SlicePipe} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {routes} from '../../app.routes';
import {Router} from '@angular/router';
import {AccountService} from '../../services/account.service';
import {BankAccount} from '../../Model/BankAccount';
import {TransactionService} from '../../services/transaction.service';

@Component({
  selector: 'transaction-section',
  imports: [
    ReactiveFormsModule,
    NgIf,
    SlicePipe,
    DatePipe,
    CurrencyPipe,
  ],
  templateUrl: './transactions-section.html',
  styleUrl: './transactions-section.scss'
})
export class TransactionsSection {

  // passed up to parent component (account-page)
  // pass-up code : in transactions-section.html
  // when a new transaction is created, inform the parent component (account-page)
  // so that it can update the account balance
  @Output() transactionCreated = new EventEmitter<{ accoutId : string, pendingAmount: number }>();

  newPaymentForm! : FormGroup;

  newDepositForm! : FormGroup;


  // passed down from parent component (account-page)
  // pass-down code : in account-page.html
  currentSelectedAccount = input<BankAccount | null>(null);

  transactionList = computed( () => this.transactionService.getTransactionList() ) ;


  myEffect = effect(
    () => {

      // refetch transactions for the currently selected account
      // when selected account changes => fetch transactions for new accounts
        if (this.currentSelectedAccount()) {

          // fetch and fill this.transactionList
          this.transactionService.fetchTransaction(this.currentSelectedAccount()!.id);
        }
    }
  )


  constructor(private formBuilder: FormBuilder,
              private transactionService: TransactionService,
              private rt : Router) {
    this.initLoginForm(formBuilder);

  }


  initLoginForm(formBuilder: FormBuilder) : void {


    this.newPaymentForm = this.formBuilder.group({
      creditAccountId: [ 0, [Validators.required] ],
      montant: [ 0, [Validators.required, Validators.min(1), Validators.max(10000)] ],
        description: [ '', [Validators.required, Validators.minLength(3), Validators.maxLength(100)] ],

      },{
        updateOn: 'blur'  // emit value and validation only on blur event
      }

    );

    this.newDepositForm = this.formBuilder.group({
        montant: [ 0, [Validators.required, Validators.min(1), Validators.max(10000)] ],

      },{
        updateOn: 'blur'  // emit value and validation only on blur event
      }

    );

  }

  ngOnInit(): void {

  }

  onDepotFormSubmit() {
    const amount = Number( this.newDepositForm.get('montant')?.value);
    const description = "Nouveau dépôt";

    this.transactionService.createDepositTransaction( this.currentSelectedAccount()!.id, amount, description).subscribe(
      {
        next: ( () => {

          alert("New Deposit Transaction creation successful");
          // update the transaction list after creating a new transaction
          this.transactionService.fetchTransaction( this.currentSelectedAccount()!.id );

          console.log('Transaction creation successful,  ');

          this.newDepositForm.reset();
        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;
          console.log('Status: ' + (errorResponse.status ));

          console.log('Transaction creation failed, message : ' + (errorResponse.error ));
          alert("Transaction creation failed: " + (errorResponse.error ));

        })
    });

  }

  onTransferSubmit() {
    const creditAccountId =  this.newPaymentForm.get('creditAccountId')?.value;
    const amount = Number( this.newPaymentForm.get('montant')?.value);
    const description = this.newPaymentForm.get('description')?.value;

    this.transactionService.createInternalTransferTransaction( this.currentSelectedAccount()!.id, creditAccountId ,  amount, description).subscribe(
      {
        next: ( () => {
          console.log('Transaction creation successful, Id: ');

          this.newPaymentForm.reset();

          // update the transaction list after creating a new transaction
          this.transactionService.fetchTransaction( this.currentSelectedAccount()!.id );

          alert("New Transaction creation successful");

          // inform the parent component (account-page) that a new transaction has been created
          this.transactionCreated.emit( { accoutId : this.currentSelectedAccount()!.id, pendingAmount : amount } );

        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;
          console.log('Status: ' + (errorResponse.status ));

          console.log('Transaction creation failed, message : ' + (errorResponse.error ));
          alert("Transaction creation failed: " + (errorResponse.error ));

        })

    });

  }



  // called when the user clicks on the "Mise a jour" button in account-page.html
  refreshTransactions() {
    if (this.currentSelectedAccount()) {
      this.transactionService.fetchTransaction(this.currentSelectedAccount()!.id);
    }
  }



}
