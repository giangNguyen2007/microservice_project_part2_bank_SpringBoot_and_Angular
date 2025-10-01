import {Component, computed, effect,  signal,  ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import { Observable} from 'rxjs';
import {User} from '../../Model/User';
import {NgIf, NgStyle, SlicePipe} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {Router} from '@angular/router';
import {AccountService} from '../../services/account.service';
import {TransactionsSection} from '../transactions-section/transactions-section';
import {BankAccount} from '../../Model/BankAccount';
import {IncomingTransactionSection} from '../incoming-transaction-section/incoming-transaction-section';
import {ConfigService} from '../../services/config.service';

@Component({
  selector: 'account-page',
  imports: [
    ReactiveFormsModule,
    NgIf,
    TransactionsSection,
    IncomingTransactionSection,
    NgStyle,
  ],
  templateUrl: './account-page.html',
  styleUrl: './account-page.scss'
})
export class AccountPage{


  createAccountForm! : FormGroup;


  @ViewChild('transactionsSection') transactionsSection!: TransactionsSection;

  loginSuccess = computed( () => this.authService.getToken() != null ) ;

  // all bank accounts of the logged in user
  bankAccounts = computed( () => this.bankAccountService.getAccounts() ) ;

  // to be passed to TransactionsSection component
  selectedAccount = signal<BankAccount | null>(null);


   options = [
    { value: 'COMPTE_COURANT', label: 'Compte Courant' },
    { value: 'COMPTE_EPARGNE', label: 'Livret A' }
  ];


  myEffect = effect(
    () => {

        if (this.loginSuccess()) {
          this.bankAccountService.fetchAccounts();
        }
    }
  )

  urlRegex = /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)/;

  constructor(private formBuilder: FormBuilder,
              public authService : AuthService,
              private bankAccountService: AccountService,
              public configService: ConfigService,
              private rt : Router) {
    this.initLoginForm(formBuilder);

  }


  initLoginForm(formBuilder: FormBuilder) : void {


    this.createAccountForm = this.formBuilder.group({
        userId: [ this.authService.getUsers()?.userId ?? '', [Validators.required, Validators.email]],
        accountType : ['Compte Courant']

      },{
        updateOn: 'blur'  // emit value and validation only on blur event
      }

    );

  }

  /**
   * Handles the selection of a bank account from the UI.
   * Updates the selected account signal and logs the selection.
   *
   * @param accountCard - The HTML element representing the account card that was clicked
   * @param account - The BankAccount object that was selected
   */
  onAccountSelect( accountCard : HTMLElement, account: BankAccount) {
    this.selectedAccount.set(account) ;
    console.log('Account selected: ' + account.id);
  }



  createAccount() {
    const userId = this.createAccountForm.get('userId')?.value;
    const accountType = this.createAccountForm.get('accountType')?.value;

    console.log('create account for userId: ' + userId + ', accountType: ' + accountType);

    this.bankAccountService.createAccount(accountType).subscribe(
      {
        next: (response => {
          console.log('Account creation successful, accountId: ' + response.id);

          this.createAccountForm.reset();
          alert("Account creation successful, accountId: " + response.id);

          this.bankAccountService.fetchAccounts();

        }),

        error: (error => {
          const errorResponse = error as HttpErrorResponse;
          console.log('Status: ' + (errorResponse.status ));

          console.log('Account creation failed, message : ' + (errorResponse.error ));
          alert("Account creation failed: " + (errorResponse.error ));

        })

      }
    )
  }



  onUpdateClick(){
    this.bankAccountService.fetchAccounts();
    this.transactionsSection.refreshTransactions();

  }

  // to be called by children, when new transaction is created
  onTransactionCreated(event: { accoutId: string; pendingAmount: number }){
    //alert('Transaction created event received in AccountPage for accountId: ' + event.accoutId + ', pendingAmount: ' + event.pendingAmount);

    this.bankAccountService.fetchAccounts();

  }






}
