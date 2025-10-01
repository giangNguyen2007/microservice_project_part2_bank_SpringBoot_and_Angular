import {Component, computed, signal} from '@angular/core';
import {Router, RouterLink, RouterLinkActive} from '@angular/router';
import {AuthService} from '../../services/auth.service';
import {NgIf} from '@angular/common';
import {TransactionService} from '../../services/transaction.service';
import {IncomingTransactionService} from '../../services/incomingTransaction.service';
import {AccountService} from '../../services/account.service';

@Component({
  selector: 'header',
  imports: [
    RouterLink,
    RouterLinkActive,
    NgIf
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss'
})
export class Header {

  isAuthenticated = computed( () => this.authService.getUsers() != null); // This should come from your auth service

  constructor(private authService: AuthService,
   private transactionService: TransactionService,
   private incomingTransactionsService: IncomingTransactionService,
   private accountService: AccountService,
              private router : Router

  ) {
  }

  onLogoutClick() {
    // Implement logout logic here
    this.authService.logout();

    this.transactionService.onLogout();
    this.incomingTransactionsService.onLogout();
    this.accountService.logoutAccounts()

    this.router.navigate(['login']);
  }

}
