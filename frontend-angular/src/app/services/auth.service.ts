// create a service FaceSnapService with the following properties and methods:
import {computed, Injectable, signal} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from '@angular/common/http';
import {catchError, Observable, tap} from 'rxjs';
import {User} from '../Model/User';
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
export class AuthService {

  // private apiUrl = 'http://localhost:8085';

  public apiUrl = "";

  users$ !: Observable<User[]>;

  private user = signal<User | null>(null) ;
  private token = signal<string | null>(null) ;
  private loginMessage = signal<string | null>(null) ;



  constructor(private http: HttpClient) {

    //this.apiUrl = this.configService.apiUrl + '/user';

    // console.log(this.apiUrl);


    const savedToken = localStorage.getItem('token');
    const savedUser = localStorage.getItem('user');

    if (savedToken && savedUser) {
      this.token.set(savedToken);
      this.user.set(JSON.parse(savedUser));
    }

  }

  // to be called by config service after loading config
  setUrl(url: string) : void {
    this.apiUrl = url + '/user';
    console.log('AuthService apiUrl set to: ' + this.apiUrl);
  }


  loginUser(name: string, email: string, password: string): Observable<LoginResponse> {
    // create a json object with email and password
    const loginData = { name, email, password };

    console.log(this.apiUrl);

    return this.http.post<LoginResponse>(this.apiUrl + '/login', loginData).pipe(
      tap(response => {

        console.log('Login successful, token received: ' + response.token);

        this.token.set(response.token);
        this.user.set({userId: response.userId, name: response.name, email: response.name, role: response.role as "USER" | "ADMIN" | null});

        // save in local storage
        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(this.user()));

      }));

  }

  registerUser(name: string, email: string, password: string): Observable<any>{
    // create a json object with email and password
    const registerData = { name, email, password };

    console.log(this.apiUrl);

    return this.http.post<any>(this.apiUrl + '/register', registerData);

  }

  logout() :  void {
    this.user.set(null);
    this.token.set(null);
    this.loginMessage.set(null);

    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }


  getToken() : string | null {

    return this.token();
  }

  getUsers() : User | null{
    return this.user();
  }

  getLoginMessage() : string | null{
    return this.loginMessage();
  }

}
