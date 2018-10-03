import { Component, OnInit, OnDestroy, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Response } from '@angular/http';

import { DataTable, Column, Header, Footer} from 'primeng/primeng';
import { LazyLoadEvent } from 'primeng/primeng';
import { Message, Growl } from 'primeng/primeng';

import { Group } from './group';
import { GroupService } from './group.service';







@Component({
    selector: 'group',
    templateUrl: 'resources/angular2/login/menu_app/group/group.html',
    styleUrls:  ['resources/angular2/login/menu_app/group/group.css'],
    directives: [ DataTable, Column, Growl ],
    providers: [ GroupService ]
})
export class GroupComponent implements OnInit
{
    title = 'Groups';
    groups: Group[];
    selectedGroup: Group;
    totalRecords: number = 0;
    msgs: Message[] = [];
    lastLazyLoadEvent: LazyLoadEvent = null;
    
    
    
    constructor(private groupService : GroupService, private router: Router)
    {
    
    }
          
    ngOnInit()
    {
        
    }
    
    onRowClick(event)
    {
        this.router.navigate(['/groups/edit/', event.data.id]);
    }
    
    onCreate(event)
    {
        this.router.navigate(['/groups/create']);
    }
    
    onDelete(perm: Group, event: any)
    {
      event.stopPropagation();
      this.groupService
          .delete(perm)
          .then((result: {}) => {
                this.groups = this.groups.filter(p => p !== perm);
                if (this.selectedGroup === perm)
                    this.selectedGroup = null;
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
        console.log('load', event);
        this.groupService.getObjects(event.first, event.rows, event.sortField, event.sortOrder, event.filters)
            .subscribe(result => { this.groups = result.data; this.totalRecords = result.total;},
                       error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
    }
    
}



