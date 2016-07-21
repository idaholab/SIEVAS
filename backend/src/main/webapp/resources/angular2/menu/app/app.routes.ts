import { provideRouter, RouterConfig } from '@angular/router';

import { AppComponent } from './app.component';
import { EmptyComponent } from './empty.component';
import { PermissionComponent } from './permission/permission.component';
import { PermissionEditComponent } from './permission/permissionedit.component';
import { GroupComponent } from './group/group.component';
import { GroupEditComponent } from './group/groupedit.component';
import { UserComponent } from './user/user.component';
import { UserEditComponent } from './user/useredit.component';


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
    },
    {
        path: 'groups',
        component: GroupComponent
    },
    {
        path: 'groups/create',
        component: GroupEditComponent
    },
    {
        path: 'groups/edit/:id',
        component: GroupEditComponent
    },
    {
        path: 'users',
        component: UserComponent
    },
    {
        path: 'users/create',
        component: UserEditComponent
    },
    {
        path: 'users/edit/:id',
        component: UserEditComponent
    }
    
];

export const APP_ROUTER_PROVIDERS = [
  provideRouter(routes)
];