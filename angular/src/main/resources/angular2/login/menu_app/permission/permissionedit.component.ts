
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { Message, Growl } from 'primeng/primeng';

import 'rxjs/add/operator/do';

import { Permission } from './permission';
import { PermissionService } from './permission.service';
import { JsonError } from '../jsonerror';

@Component({
  selector: 'permission-detail',
  templateUrl: 'resources/angular2/login/menu_app/permission/permissionedit.html',
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
            this.permissionService.getObjectById(id).then(perm => this.permission = perm)
                .catch(error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
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
          .then((perm: Permission) => {
                this.permission = perm;
                this.close.emit(perm);
                this.router.navigate(['/permissions']);
          })
          .catch((error: JsonError) => this.msgs.push({severity:'error', summary:'Error Saving', detail:error.error})); 
          
  }
  
  onSubmit()
  {
      return this.form.valid;
      
  }

  
  
}
