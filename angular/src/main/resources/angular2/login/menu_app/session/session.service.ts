import { Injectable } from '@angular/core';

import { Http } from '@angular/http';

import { SIEVASSession } from './session';
import { BaseService } from '../BaseService';


@Injectable()
export class SessionService extends BaseService<number, SIEVASSession>
{
    constructor(http: Http)
    {
        super(http);
    }
    
    getBaseUrl()
    {
        return 'api/sessions/';
    }
    
}