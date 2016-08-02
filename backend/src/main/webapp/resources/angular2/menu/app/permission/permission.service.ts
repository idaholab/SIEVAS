import { Injectable } from '@angular/core';

import { Http } from '@angular/http';

import { Permission } from './permission';
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