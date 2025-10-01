import {Component, OnInit, signal} from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Header} from './components/header/header';

@Component({
  selector: 'app-root',
  imports : [
    Header,
    RouterOutlet,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App{

}
