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


@Injectable()
export class PermissionService {
    
    constructor(private http: Http) {}

    getPermissions(first: number, rows: number, sortField: string, sortOrder: number, filters: any)
    {
        
        let url = 'api/permissions/?start=' + encodeURIComponent(first.toString()) + '&rows=' + encodeURIComponent(rows.toString())
                                    + '&sortField=' + ((sortField) ? encodeURIComponent(sortField)  : '')
                                    + '&sortOrder=' + encodeURIComponent(sortOrder.toString())
                                    + '&filters=' + ((filters) ? encodeURIComponent(JSON.stringify(filters)) : '');
        return this.http.get(url)
                    .map(response => response.json());
    }
    
    getPermissionById(id: number)
    {
        
        let url = '/api/permissions/' + encodeURIComponent(id.toString()) + '?_dc=' + encodeURIComponent(Math.random().toString());
        return this.http.get(url).toPromise().then(result => result.json());
    }
    
    save(perm: Permission)
    {
        if (perm.id)
            return this.put(perm);
        else
            return this.post(perm);
    }
    
    put(perm: Permission)
    {
        let body = JSON.stringify(perm);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        
        let url = `api/permissions/${perm.id}`;
        return this.http.put(url, body, options)
            .toPromise().then((result) => result.json())
            .catch(this.handleError);
    }
    
    post(perm: Permission)
    {
        let body = JSON.stringify(perm);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        
        let url = `api/permissions/`;
        return this.http.post(url, body, options)
            .toPromise().then((result: Response) => result.json())
            .catch(this.handleError);
    }
    
    delete(perm: Permission)
    {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        let url = `api/permissions/${perm.id}`;

        return this.http
                   .delete(url, headers)
                    .toPromise()
                    .then((result: Response) => result.json())
                   .catch(this.handleError);
      }
    
    private handleError (response: Response)
    {
        // In a real world app, we might use a remote logging infrastructure
        // We'd also dig deeper into the error to get a better message
        let json: JsonError = response.json();
        if (json && json.error)
            return json;
        else
            Observable.throw(response.status ? `${response.status} - ${response.statusText}` : 'Server error');
        /*console.log('ERR', response);
        let json: JsonError = response.json();
        console.log('JSON',json);
        let errMsg = response.status ? `${response.status} - ${response.statusText}` : 'Server error';
        if (json && json.error)
            errMsg = json.error;
          
        return Observable.throw(errMsg);*/
    }
}