import {Component, computed, effect, inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {config, map, Observable} from 'rxjs';
import {User} from '../../Model/User';
import {AsyncPipe, DatePipe, NgIf} from '@angular/common';
import {AuthService} from '../../services/auth.service';
import {HttpErrorResponse} from '@angular/common/http';
import {routes} from '../../app.routes';
import {Router} from '@angular/router';
import {ConfigService} from '../../services/config.service';

@Component({
  selector: 'login-page',
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  templateUrl: './login-page.html',
  styleUrl: './login-page.scss'
})
export class LoginPage implements OnInit {

  // authService = inject(FaceSnapService) ;

  loginForm! : FormGroup;

  users$! : Observable<User[]>;

  loginSuccess = computed( () => this.authService.getToken() != null ) ;

  loginMessage = computed( () => this.authService.getLoginMessage() ) ;


  myEffect = effect(
    () => {
      if (this.loginSuccess()) {
        this.loginForm.reset();
        console.log('execute effect - login success, navigate to home page');
      }

    }
  )


  ngOnInit() {
    this.authService.setUrl(this.configService.apiUrl);
  }


  urlRegex = /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)/;

  constructor(private formBuilder: FormBuilder, private authService : AuthService, private rt : Router, private configService : ConfigService) {
    this.initLoginForm(formBuilder);

  }


  initLoginForm(formBuilder: FormBuilder) : void {

    this.loginForm = this.formBuilder.group({
        email: ['', [Validators.required, Validators.email]],
        userName: ['', [Validators.required, Validators.maxLength(20)]],
        password: ['', [Validators.required, Validators.minLength(6)]]
        //url: ['', [Validators.required, Validators.pattern(this.urlRegex)]],

      },{
        updateOn: 'blur'  // emit value and validation only on blur event
      }

    );

  }

  onLogin() {
    const email = this.loginForm.get('email')?.value;
    const username = this.loginForm.get('userName')?.value;
    const password = this.loginForm.get('password')?.value;

    // Here you can handle the login logic, e.g., call an authentication service
    // console.log('email:', email);
    // console.log('Username:', username);
    // console.log('Password:', password);

    //console.log('send login to server for user ' + username + ' with password ' + password) ;

    this.authService.loginUser(username, email, password).subscribe({
      next: (response => {

        alert("Login reussi ");

        // how to navigate to home page
        this.rt.navigate(['comptes']);


      }),

      error: (error => {
        const errorResponse = error as HttpErrorResponse;

        alert("Login failed: " + errorResponse.error);

        console.log('Status: ' + (errorResponse.status ) );

        console.log('login failed: ' + (errorResponse.error ) );
      })

    });
  }

  onLogout() : void {
      this.authService.logout();
  }

}
