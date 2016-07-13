
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { Message, Growl } from 'primeng/primeng';

import 'rxjs/add/operator/do';
import { ErrorObservable } from 'rxjs/observable/ErrorObservable';


import { Permission } from './permission';
import { PermissionService } from './permission.service';

@Component({
  selector: 'permission-detail',
  templateUrl: 'resources/angular2/menu/app/permission/permissionedit.html',
  providers: [ PermissionService ],
  directives: [Growl]
})
export class PermissionEditComponent implements OnInit, OnDestroy
{
  @Input()
  permission: Permission;
  
  @Output()
  close = new EventEmitter();
  
  sub: any;
  form: FormGroup;
  msgs: Message[] = [];
  

  constructor(
    private permissionService: PermissionService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder) {
    
      this.form = this.formBuilder.group({
         'permissionName': ['', Validators.required],
         'description': ['', Validators.required],
         
      });
  }

  ngOnInit()
  {
    this.sub = this.route.params.subscribe(params => {
        if (params['id'] !== undefined)
        {
            let id = +params['id'];
            this.permissionService.getPermissionById(id).then(perm => this.permission = perm);
        }
        else
        {
            this.permission = new Permission();
        }
    });
  }

  ngOnDestroy()
  {
    this.sub.unsubscribe();
  }

  onCancel()
  {
    this.router.navigate(['/permissions']);
  }
  
  onSave()
  {
      this.permissionService.save(this.permission)
          .then((perm: any) => {
              console.log('onSave',perm);
              
              if (perm.typeName && perm.typeName == "JsonError")
                this.msgs.push({severity:'error', summary:'Error Saving', detail:perm.error});
              else if (perm.constructor && perm.constructor.name === 'ErrorObservable')
                this.msgs.push({severity:'error', summary:'Error Saving', detail:perm.error});
              else
              {
                this.permission = perm;
                this.close.emit(perm);
                this.router.navigate(['/permissions']);
              }
          })
          .catch(error => alert(error));
          
  }
  
  onSubmit()
  {
      return this.form.valid;
      
  }
//  onKeyDown(event: KeyboardEvent)
//  {
//      console.log(event);
//      if(event.keyCode == 13)
//      {
//          event.stopPropagation();
//          if (this.form.valid)
//            this.onSave();
//          else
//            alert("error");
//      }
//  }
  
  
}