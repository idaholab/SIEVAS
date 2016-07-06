import { Component, OnInit, OnDestroy } from '@angular/core';

import { HTTP_PROVIDERS } from '@angular/http';
import { Http } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';
import { ROUTER_DIRECTIVES } from '@angular/router';

import { MenuComponent } from './menu.component';






@Component({
    selector: 'my-app',
    template: `
                <router-outlet></router-outlet>
                `,
    providers: [ HTTP_PROVIDERS ],
    directives: [ ROUTER_DIRECTIVES ],
    precompile: [ MenuComponent ]
})
export class AppComponent implements OnInit
{
    title = 'LIVE2';
      
    
    constructor(public http: Http)
    {
        
    }
    
    ngOnInit()
    {
        
    }
    
}
