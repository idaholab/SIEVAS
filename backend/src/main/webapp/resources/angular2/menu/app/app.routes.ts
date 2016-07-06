import { provideRouter, RouterConfig } from '@angular/router';

import { AppComponent } from './app.component';
import { MenuComponent } from './menu.component';


export const routes: RouterConfig = [
    {
        path: '',
        redirectTo: '/menu',
        pathMatch: 'full'
    },
    {
        path: 'menu',
        component: MenuComponent
    }
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];