import { Component, OnInit, OnDestroy, EventEmitter } from '@angular/core';

import { HTTP_PROVIDERS } from '@angular/http';
import { Http } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';
import { ROUTER_DIRECTIVES, Router } from '@angular/router'; 

import { Menubar, Menu, MenuItem } from 'primeng/primeng';

import { EmptyComponent } from './empty.component';
import { PermissionComponent } from './permission/permission.component';
import { PermissionEditComponent } from './permission/permissionedit.component';


@Component({
    selector: 'my-app',
    templateUrl: 'resources/angular2/menu/app/mainmenu.html',
    providers: [ HTTP_PROVIDERS ],
    directives: [ ROUTER_DIRECTIVES, Menubar, Menu],
    precompile: [ EmptyComponent, PermissionComponent, PermissionEditComponent ]
})
export class AppComponent implements OnInit
{
    title = 'LIVE2';
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
                    }
                ]
            }
        ];
    }
    
}
