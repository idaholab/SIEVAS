import { Component, OnInit, OnDestroy, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Response } from '@angular/http';

import { DataTable, Column, Header, Footer} from 'primeng/primeng';
import { LazyLoadEvent } from 'primeng/primeng';
import { Message, Growl } from 'primeng/primeng';

import { Permission } from './permission';
import { JsonError } from '../jsonerror';
import { PermissionService } from './permission.service';







@Component({
    selector: 'permission',
    templateUrl: 'resources/angular2/menu/app/permission/permission.html',
    styleUrls:  ['resources/angular2/menu/app/permission/permission.css'],
    directives: [ DataTable, Column, Growl ],
    providers: [ PermissionService ]
})
export class PermissionComponent implements OnInit
{
    title = 'Permissions';
    permissions: Permission[];
    selectedPermission: Permission;
    totalRecords: number = 0;
    msgs: Message[] = [];
    lastLazyLoadEvent: LazyLoadEvent = null;
    
    
    
    constructor(private permissionService : PermissionService, private router: Router)
    {
    
    }
          
    ngOnInit()
    {
        
    }
    
    onRowClick(event)
    {
        this.router.navigate(['/permissions/edit/', event.data.id]);
    }
    
    onCreate(event)
    {
        this.router.navigate(['/permissions/create']);
    }
    
    onDelete(perm: Permission, event: any)
    {
      event.stopPropagation();
      this.permissionService
          .delete(perm)
          .then((result: any) => {
              if (result.typeName && result.typeName == 'JsonError')
              {
                if (result.error)
                  this.msgs.push({severity:'error', summary:'Error Message', detail:result.error});
                else
                  this.msgs.push({severity:'error', summary:'Error Message', detail:"Unknown Error"});
              }
              else
              {
                this.permissions = this.permissions.filter(p => p !== perm);
                if (this.selectedPermission === perm)
                  this.selectedPermission = null;
                if (this.lastLazyLoadEvent)
                    this.loadData(this.lastLazyLoadEvent);
              }
              
          }).catch(error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
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
        this.permissionService.getPermissions(event.first, event.rows, event.sortField, event.sortOrder, event.filters).subscribe(result => {this.permissions = result.data; this.totalRecords = result.total;});
    }
    
}



