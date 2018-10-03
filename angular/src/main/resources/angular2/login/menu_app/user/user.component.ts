import { Component, OnInit, OnDestroy, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Response } from '@angular/http';

import { DataTable, Column, Header, Footer} from 'primeng/primeng';
import { LazyLoadEvent } from 'primeng/primeng';
import { Message, Growl } from 'primeng/primeng';

import { User } from './user';
import { UserService } from './user.service';







@Component({
    selector: 'user',
    templateUrl: 'resources/angular2/login/menu_app/user/user.html',
    styleUrls:  ['resources/angular2/login/menu_app/user/user.css'],
    directives: [ DataTable, Column, Growl ],
    providers: [ UserService ]
})
export class UserComponent implements OnInit
{
    title = 'Users';
    users: User[];
    selectedUser: User;
    totalRecords: number = 0;
    msgs: Message[] = [];
    lastLazyLoadEvent: LazyLoadEvent = null;
    
    
    
    constructor(private userService : UserService, private router: Router)
    {
    
    }
          
    ngOnInit()
    {
        
    }
    
    onRowClick(event)
    {
        this.router.navigate(['/users/edit/', event.data.id]);
    }
    
    onCreate(event)
    {
        this.router.navigate(['/users/create']);
    }
    
    onDelete(user: User, event: any)
    {
      event.stopPropagation();
      this.userService
          .delete(user)
          .then((result: {}) => {
              this.users = this.users.filter(p => p !== user);
              if (this.selectedUser === user)
                  this.selectedUser = null;
                if (this.lastLazyLoadEvent)
                    this.loadData(this.lastLazyLoadEvent);
          }).catch(error => this.msgs.push({severity:'error', summary:'Error Message', detail:error.error}));
    }
    
    
    loadData(event: LazyLoadEvent)
    {
        //event.first = First row offset
        //event.rows = Number of rows per page
        //event.sortField = Field name to sort in single sort mode
        //event.sortOrder = Sort order as number, 1 for asc and -1 for dec in single sort mode
        //multiSortMeta: An array of SortMeta objects used in multiple columns sorting. Each SortMeta has field and order properties.
        //filters: Filters object having field as key and filter value, filter matchMode as value
        this.lastLazyLoadEvent = event;
        this.userService.getObjects(event.first, event.rows, event.sortField, event.sortOrder, event.filters)
            .subscribe(result => { this.users = result.data; this.totalRecords = result.total;},
                       error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
    }
    
}



