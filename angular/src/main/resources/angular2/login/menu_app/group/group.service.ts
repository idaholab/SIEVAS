import { Injectable } from '@angular/core';

import { Http } from '@angular/http';

import { Group } from './group';
import { BaseService } from '../BaseService';


@Injectable()
export class GroupService extends BaseService<number, Group>
{
    constructor(http: Http)
    {
        super(http);
    }
    
    getBaseUrl()
    {
        return 'api/permissiongroups/';
    }
    
}