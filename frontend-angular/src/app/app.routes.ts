import { Routes } from '@angular/router';
import {LoginPage} from './components/login-page/login-page';
import {AccountPage} from './components/account-page/account-page';
import {RegisterPage} from './components/register-page/register-page';

export const routes: Routes = [

  { path: '', component: LoginPage },
  { path: 'login', component: LoginPage },
  { path: 'register', component: RegisterPage },
  { path: 'comptes', component: AccountPage }

];
