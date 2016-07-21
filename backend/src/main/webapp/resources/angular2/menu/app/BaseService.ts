/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import { Component, OnInit, OnDestroy, Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';

import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/toPromise';
import 'rxjs/add/operator/do';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/observable/throw';

import { Http, Response, Headers, RequestOptions } from '@angular/http';

import { ResultSet } from './resultset';
import { JsonError } from './jsonerror';
import { IIdentifier } from './IIdentifier';

@Injectable()
export abstract class BaseService<K, T extends IIdentifier<K> >
{
    public abstract getBaseUrl();
    
    constructor(protected http: Http) {}

    getObjects(first: number, rows: number, sortField: string, sortOrder: number, filters: any) : Observable<ResultSet>
    {
        
        let url = this.getBaseUrl() + '?start=' + encodeURIComponent(first.toString()) + '&count=' + encodeURIComponent(rows.toString())
                                    + '&sortField=' + ((sortField) ? encodeURIComponent(sortField)  : '')
                                    + '&sortOrder=' + encodeURIComponent(sortOrder.toString())
                                    + '&filters=' + ((filters) ? encodeURIComponent(JSON.stringify(filters)) : '');
        return this.http.get(url)
                    .map(response => response.json());
                    //.map(result => (new ResultSet(result.data, result.total)));
    }
    
    getObjectById(id: K): Promise<T>
    {
        
        let url = this.getBaseUrl() + encodeURIComponent(id.toString()) + '?_dc=' + encodeURIComponent(Math.random().toString());
        return this.http.get(url).toPromise().then(result => result.json());
    }
    
    save(obj: T) : Promise<T>
    {
        if (obj.id)
            return this.put(obj);
        else
            return this.post(obj);
    }
    
    put(obj: T) : Promise<T>
    {
        let body = JSON.stringify(obj);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        
        let url = this.getBaseUrl() + `${obj.id}`;
        return this.http.put(url, body, options)
            .toPromise().then((result) => result.json())
            .catch(this.handleError);
    }
    
    post(obj: T) : Promise<T>
    {
        let body = JSON.stringify(obj);
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        
        let url = this.getBaseUrl();
        return this.http.post(url, body, options)
            .toPromise().then((result: Response) => result.json())
            .catch(this.handleError);
    }
    
    delete(obj: T) : Promise<{}>
    {
        let headers = new Headers({ 'Content-Type': 'application/json' });
        let options = new RequestOptions({ headers: headers });
        let url = this.getBaseUrl() + `${obj.id}`;

        return this.http
                   .delete(url, options)
                    .toPromise()
                    .then((result: Response) => result.json())
                   .catch(this.handleError);
      }
    
    private handleError (response: Response) : JsonError
    {
        let json: JsonError = response.json();
        if (json && json.error)
            throw new JsonError(json.error);
        else
            throw new JsonError(response.status ? `${response.status} - ${response.statusText}` : 'Server error');
    }
}