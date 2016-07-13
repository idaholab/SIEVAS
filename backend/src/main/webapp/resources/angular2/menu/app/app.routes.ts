import { provideRouter, RouterConfig } from '@angular/router';

import { AppComponent } from './app.component';
import { EmptyComponent } from './empty.component';
import { PermissionComponent } from './permission/permission.component';
import { PermissionEditComponent } from './permission/permissionedit.component';


export const routes: RouterConfig = [
    /*{
        path: '',
        redirectTo: '/menu',
        pathMatch: 'full'
    },*/
    {
        path: '',
        component: EmptyComponent
    },
    {
        path: 'permissions',
        component: PermissionComponent
    },
    {
        path: 'permissions/create',
        component: PermissionEditComponent
    },
    {
        path: 'permissions/edit/:id',
        component: PermissionEditComponent
    }
    
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];