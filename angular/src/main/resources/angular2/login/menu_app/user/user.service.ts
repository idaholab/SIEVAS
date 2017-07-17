import { Injectable } from '@angular/core';

import { Http } from '@angular/http';

import { User } from './user';
import { BaseService } from '../BaseService';


@Injectable()
export class UserService extends BaseService<number, User>
{
    constructor(http: Http)
    {
        super(http);
    }
    
    getBaseUrl()
    {
        return 'api/users/';
    }
    
}