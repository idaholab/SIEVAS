import { Component, OnInit, OnDestroy, Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/observable/throw';

import { Http, Response, Headers, RequestOptions } from '@angular/http';

import { Permission } from './permission';
import { ResultSet } from '../resultset';
import { JsonError } from '../jsonerror';
import { BaseService } from '../BaseService';


@Injectable()
export class PermissionService extends BaseService<number, Permission>
{
    constructor(http: Http)
    {
        super(http);
    }
    
    getBaseUrl()
    {
        return 'api/permissions/';
    }
    
}