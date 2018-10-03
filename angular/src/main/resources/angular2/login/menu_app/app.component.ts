import { Component, OnInit, OnDestroy, EventEmitter } from '@angular/core';

import { HTTP_PROVIDERS } from '@angular/http';
import { Http } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';
import { ROUTER_DIRECTIVES, Router } from '@angular/router'; 

import { Menubar, Menu, MenuItem } from 'primeng/primeng';

import { EmptyComponent } from './empty.component';
import { PermissionComponent } from './permission/permission.component';
import { PermissionEditComponent } from './permission/permissionedit.component';
import { GroupComponent } from './group/group.component';
import { GroupEditComponent } from './group/groupedit.component';
import { UserComponent } from './user/user.component';
import { UserEditComponent } from './user/useredit.component';
import { SessionComponent } from './session/session.component';
import { SessionEditComponent } from './session/sessionedit.component';



@Component({
    selector: 'my-app',
    templateUrl: 'resources/angular2/login/menu_app/mainmenu.html',
    providers: [ HTTP_PROVIDERS ],
    directives: [ ROUTER_DIRECTIVES, Menubar, Menu],
    precompile: [ EmptyComponent, PermissionComponent, PermissionEditComponent, GroupComponent, GroupEditComponent, UserComponent, UserEditComponent, SessionComponent, SessionEditComponent ]
})
export class AppComponent implements OnInit
{
    title = 'SIEVAS';
    items: MenuItem[];
    menuClick = new EventEmitter();
      
    
    constructor(public http: Http, private router: Router)
    {
        
    }
    
    onMenuQuit(event)
    {
        window.location.href = 'logout';
    }
    
    onMenuPermissions(event)
    {
        let link = ['/permissions'];
        this.router.navigate(link);
    }
    
    onMenuGroups(event)
    {
        let link = ['/groups'];
        this.router.navigate(link);
    }
    
    onMenuUsers(event)
    {
        let link = ['/users'];
        this.router.navigate(link);
    }
    
    onMenuSessions(event)
    {
        let link = ['/sessions'];
        this.router.navigate(link);
    }
    
    ngOnInit()
    {
        this.items = [
            {
                label: 'File',
                items: [
                    {
                        label: 'New User', 
                        icon: 'fa-plus'
                    },
                    {label: 'Open'},
                    {label: 'Quit', command: (event) => this.onMenuQuit(event) }
                ]
            },
            {
                label: 'Admin',
                items:[
                    {
                        label: 'Permissions',
                        command: (event) => this.onMenuPermissions(event)  
                    },
                    {
                        label: 'Groups',
                        command: (event) => this.onMenuGroups(event)  
                    },
                    {
                        label: 'Users',
                        command: (event) => this.onMenuUsers(event)  
                    },
                    {
                        label: 'Sessions',
                        command: (event) => this.onMenuSessions(event)  
                    }
                ]
            }
        ];
    }
    
}
