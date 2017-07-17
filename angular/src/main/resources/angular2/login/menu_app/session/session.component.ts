import { Component, OnInit, OnDestroy, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Response } from '@angular/http';

import { DataTable, Column, Header, Footer} from 'primeng/primeng';
import { LazyLoadEvent } from 'primeng/primeng';
import { Message, Growl } from 'primeng/primeng';

import { SIEVASSession } from './session';
import { SessionService } from './session.service';







@Component({
    selector: 'group',
    templateUrl: 'resources/angular2/menu/app/session/session.html',
    styleUrls:  ['resources/angular2/menu/app/session/session.css'],
    directives: [ DataTable, Column, Growl ],
    providers: [ SessionService ]
})
export class SessionComponent implements OnInit
{
    title = 'Sessions';
    sessions: SIEVASSession[];
    selectedSession: SIEVASSession;
    totalRecords: number = 0;
    msgs: Message[] = [];
    lastLazyLoadEvent: LazyLoadEvent = null;
    
    
    
    constructor(private sessionService : SessionService, private router: Router)
    {
    
    }
          
    ngOnInit()
    {
        
    }
    
    onRowClick(event)
    {
        this.router.navigate(['/sessions/edit/', event.data.id]);
    }
    
    onCreate(event)
    {
        this.router.navigate(['/sessions/create']);
    }
    
    onDelete(session: SIEVASSession, event: any)
    {
      event.stopPropagation();
      this.sessionService
          .delete(session)
          .then((result: {}) => {
                this.sessions = this.sessions.filter(s => s !== session);
                if (this.selectedSession === session)
                    this.selectedSession = null;
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
        this.sessionService.getObjects(event.first, event.rows, event.sortField, event.sortOrder, event.filters)
            .subscribe(result => { this.sessions = result.data; this.totalRecords = result.total;},
                       error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
    }
    
}



