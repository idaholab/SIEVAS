import { Component, OnInit, OnDestroy } from '@angular/core';

import { HTTP_PROVIDERS } from '@angular/http';
import { Http } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';

import { Menubar, Menu, MenuItem } from 'primeng/primeng';







@Component({
    selector: 'my-menu',
    templateUrl: 'resources/angular2/menu/app/mainmenu.html',
    providers: [ HTTP_PROVIDERS ],
    directives: [Menubar, Menu]
})
export class MenuComponent implements OnInit
{
    title = 'Main Menu';
    items: MenuItem[];
      
    
    constructor(public http: Http)
    {
        
    }
    
    onMenuQuit()
    {
        window.location.href = 'logout';
    }
    
    ngOnInit()
    {
        this.items = [
            {
                label: 'File',
                items: [{
                        label: 'New User', 
                        icon: 'fa-plus'
                    },
                    {label: 'Open'},
                    {label: 'Quit', command: this.onMenuQuit}
                ]
            },
            {
                label: 'Edit',
                icon: 'fa-edit',
                items: [
                    {label: 'Undo', icon: 'fa-mail-forward'},
                    {label: 'Redo', icon: 'fa-mail-reply'}
                ]
            }
        ];
    }
    
}

