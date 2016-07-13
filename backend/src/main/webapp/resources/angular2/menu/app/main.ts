import { bootstrap }    from '@angular/platform-browser-dynamic'
import { disableDeprecatedForms, provideForms } from '@angular/forms';
import { HTTP_PROVIDERS } from '@angular/http';

import { AppComponent } from './app.component'
import { APP_ROUTER_PROVIDERS } from './app.routes';


bootstrap(AppComponent,[ HTTP_PROVIDERS,  APP_ROUTER_PROVIDERS,
  disableDeprecatedForms(),
  provideForms()
  
]).catch((err:any) => console.error(err));


