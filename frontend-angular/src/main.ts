import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import {ConfigService} from './app/services/config.service';

export function initConfig(configService: ConfigService) {
  return () => configService.loadConfig();
}

bootstrapApplication(App, appConfig)
  .catch((err) => console.error(err));
