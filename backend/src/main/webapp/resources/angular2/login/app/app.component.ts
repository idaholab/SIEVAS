import { Component, OnInit, OnDestroy } from '@angular/core';

import { HTTP_PROVIDERS } from '@angular/http';
import { Http } from '@angular/http';
import { Headers, RequestOptions } from '@angular/http';



import { LoginInfo } from './logininfo';


@Component({
    selector: 'my-app',
    templateUrl: 'resources/angular2/login/app/login-form.html',
    providers: [ HTTP_PROVIDERS ]
})
export class AppComponent implements OnInit
{
    title = 'Login';
    userInfo: LoginInfo = new LoginInfo();
    error = null;
    logout = null;

    ngOnInit()
    {
        this.error = this.getParameterByName("error");
        this.logout = this.getParameterByName("logout");
    }
    
      
    
    constructor(public http: Http)
    {
        
    }
    
    private getParameterByName(name)
    {
        let url = window.location.href;
        name = name.replace(/[\[\]]/g, "\\$&");
        let regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)"),
            results = regex.exec(url);
        if (!results)
            return null;
        if (!results[2])
            return '';
        return decodeURIComponent(results[2].replace(/\+/g, " "));
    }
    
    private getCookie(name: string) {
        let ca: Array<string> = document.cookie.split(';');
        let caLen: number = ca.length;
        let cookieName = name + "=";
        let c: string;

        for (let i: number = 0; i < caLen; i += 1) {
            c = ca[i].replace(/^\s\+/g, "");
            if (c.indexOf(cookieName) == 0) {
                return c.substring(cookieName.length, c.length);
            }
        }
        return "";
    }

    private post(path, params, method)
    {
        method = method || "post"; // Set method to post by default if not specified.

        // The rest of this code assumes you are not using a library.
        // It can be made less wordy if you use one.
        var form = document.createElement("form");
        form.setAttribute("method", method);
        form.setAttribute("action", path);

        for(var key in params)
        {
            if(params.hasOwnProperty(key))
            {
                let hiddenField = document.createElement("input");
                hiddenField.setAttribute("type", "hidden");
                hiddenField.setAttribute("name", key);
                hiddenField.setAttribute("value", params[key]);

                form.appendChild(hiddenField);
             }
        }
        let csrf = this.getCookie("XSRF-TOKEN");
        let hiddenField = document.createElement("input");
        hiddenField.setAttribute("type", "hidden");
        hiddenField.setAttribute("name", "_csrf");
        hiddenField.setAttribute("value", csrf);
        form.appendChild(hiddenField);

        document.body.appendChild(form);
        form.submit();
    }

    onSubmit(frmLogin)
    {
        this.post('login',frmLogin.value,"post");
    }
}
