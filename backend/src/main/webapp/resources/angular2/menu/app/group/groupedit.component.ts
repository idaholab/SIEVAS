
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { Message, Growl } from 'primeng/primeng';

import 'rxjs/add/operator/do';

import { ResultSet } from '../resultset';
import { Group } from './group';
import { Permission } from '../permission/permission';
import { GroupService } from './group.service';
import { PermissionService } from '../permission/permission.service';
import { JsonError } from '../JsonError';

@Component({
  selector: 'group-detail',
  templateUrl: 'resources/angular2/menu/app/group/groupedit.html',
  styleUrls:  ['resources/angular2/menu/app/group/group.css'],
  providers: [ GroupService, PermissionService ],
  directives: [Growl]
})
export class GroupEditComponent implements OnInit, OnDestroy
{
  @Input()
  group: Group;
  
  @Output()
  close = new EventEmitter();
  
  sub: any;
  form: FormGroup;
  msgs: Message[] = [];
  permissions: Permission[] = [];
  

  constructor(
    private groupService: GroupService,
    private permissionService: PermissionService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder) {
    
      this.form = this.formBuilder.group({
         'groupName': ['', Validators.required],
         'description': ['', Validators.required],
         
      });
  }

  ngOnInit()
  {
      console.log('load perms');
      this.permissionService.getObjects(0, -1, "permissionName", 1, null).subscribe((result: any) => {
          console.log('perm result',result);
          this.permissions = result.data;
      });
      console.log('END load perms');
    this.sub = this.route.params.subscribe(params => {
        if (params['id'] !== undefined)
        {
            let id = +params['id'];
            this.groupService.getObjectById(id).then(group => this.group = group)
                .catch(error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
        }
        else
        {
            this.group = new Group();
        }
    });
  }

  ngOnDestroy()
  {
    this.sub.unsubscribe();
  }

  onCancel()
  {
    this.router.navigate(['/groups']);
  }
  
  onSave()
  {
      this.groupService.save(this.group)
          .then((group: Group) => {
                this.group = group;
                this.close.emit(group);
                this.router.navigate(['/groups']);
          })
          .catch((error: JsonError) => this.msgs.push({severity:'error', summary:'Error Saving', detail:error.error})); 
          
  }
  
  onSubmit()
  {
      return this.form.valid;
      
  }

    
  permExists(id): boolean
  {
      for (let ii = 0; ii < this.group.permissionCollection.length;ii++)
      {
          if (this.group.permissionCollection[ii].id == id)
            return true;
      }
      return false;
  }
  
  togglePermission(id)
  {
      let found: boolean = false;
      for (let ii = 0; ii < this.group.permissionCollection.length;ii++)
      {
          if (this.group.permissionCollection[ii].id == id)
          {
              this.group.permissionCollection.splice(ii,1);
              found = true;
              break;
          }
      }
      if (!found)
      {
          for (let ii = 0; ii < this.permissions.length;ii++)
          {
              if (this.permissions[ii].id == id)
              {
                  this.group.permissionCollection.push(this.permissions[ii]);
                  break;
              }
          }
          
      }
  }

  
  
}