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
  selector: 'register-page',
  imports: [
    ReactiveFormsModule,
    NgIf,
  ],
  templateUrl: './register-page.html',
  styleUrl: './register-page.scss'
})
export class RegisterPage  implements OnInit{

  // authService = inject(FaceSnapService) ;

  router! : Router

  registerForm! : FormGroup;

  users$! : Observable<User[]>;

  loginSuccess = computed( () => this.authService.getToken() != null ) ;

  myEffect = effect(
    () => {
      if (this.loginSuccess()) {
        this.registerForm.reset();
        console.log('execute effect - login success, navigate to home page');
      }

    }
  )


  ngOnInit() {
    this.authService.setUrl(this.configService.apiUrl);
  }


  urlRegex = /(http(s)?:\/\/.)?(www\.)?[-a-zA-Z0-9@:%._+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_+.~#?&/=]*)/;

  constructor(private formBuilder: FormBuilder, private authService : AuthService, private rt : Router, private configService : ConfigService) {
    this.initRegisterForm(formBuilder);
    this.router = rt;

  }


  initRegisterForm(formBuilder: FormBuilder) : void {

    this.registerForm = this.formBuilder.group({
        email: ['', [Validators.required, Validators.email]],
        userName: ['', [Validators.required, Validators.maxLength(20)]],
        password: ['', [Validators.required, Validators.minLength(6)]]
        //url: ['', [Validators.required, Validators.pattern(this.urlRegex)]],

      },{
        updateOn: 'blur'  // emit value and validation only on blur event
      }

    );

  }

  onRegisterClick() {
    const email = this.registerForm.get('email')?.value;
    const username = this.registerForm.get('userName')?.value;
    const password = this.registerForm.get('password')?.value;

    // Here you can handle the login logic, e.g., call an authentication service
    console.log('Username:', username);
    console.log('Password:', password);

    console.log('send register ' + username + ' with password ' + password) ;

    this.authService.registerUser(username, email, password).subscribe({
      next: (user) => {
        alert("Register successful, please log in");

      },
      error: (err: HttpErrorResponse) => {

        const errorResponse = err as HttpErrorResponse;


        alert("Register failed: " + errorResponse.error);
      }

    });
  }

}
