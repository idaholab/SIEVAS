
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators, FormGroup, FormControl, REACTIVE_FORM_DIRECTIVES } from '@angular/forms';

import { Message, Growl } from 'primeng/primeng';

import 'rxjs/add/operator/do';

import { ResultSet } from '../resultset';

import { User } from './user';
import { Group } from '../group/group';
import { UserService } from './user.service';
import { GroupService } from '../group/group.service';
import { JsonError } from '../JsonError';
import { LongValidator, NumberValidator, FloatValidator } from '../number.validator';

@Component({
  selector: 'group-detail',
  templateUrl: 'resources/angular2/menu/app/user/useredit.html',
  styleUrls:  ['resources/angular2/menu/app/user/user.css'],
  providers: [ UserService, GroupService ],
  directives: [ Growl, LongValidator, NumberValidator, FloatValidator, REACTIVE_FORM_DIRECTIVES ]
})
export class UserEditComponent implements OnInit, OnDestroy
{
  @Input()
  user: User;
  
  @Output()
  close = new EventEmitter();
  
  sub: any;
  form: FormGroup;
  msgs: Message[] = [];
  groups: Group[] = [];
  username: FormControl;
  password: FormControl;
  password2: FormControl;
  firstName: FormControl;
  lastName: FormControl;
  edipi: FormControl;
  

  constructor(
      private userService: UserService,
    private groupService: GroupService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder) {
    
      this.username = new FormControl('username', Validators.required);
      this.password = new FormControl('password', Validators.minLength(6));
      this.password2 = new FormControl('password2');
      this.firstName = new FormControl('firstName', Validators.required);
      this.lastName = new FormControl('lastName', Validators.required);
      this.edipi = new FormControl('edipi', LongValidator.validLong);
      this.form = this.formBuilder.group({
         'id': [''],
         'username': this.username,
          'passwordGroup': this.formBuilder.group({
              'password': this.password,
              'password2': this.password2
         }, { validator: this.equalValidator }),
          'firstName': this.firstName,
         'lastName': this.lastName,
         'edipi': this.edipi,
         'enabled': [''],
         'locked': [''],
         'expired': ['']
      });
  }
  
  equalValidator({value}: any): {[key: string]: any}
  //equalValidator(value)
  {
      console.log('hello',value);
      
    const [first, ...rest] = Object.keys(value || {});
    const valid = rest.every(v => value[v] === value[first]);
    let result = valid ? null : {equal: true};
    console.log('result',result);
    return result;
  }

  ngOnInit()
  {
      this.groupService.getObjects(0, -1, "groupName", 1, null).subscribe((result: any) => {
          this.groups = result.data;
      });
    this.sub = this.route.params.subscribe(params => {
        if (params['id'] !== undefined)
        {
            let id = +params['id'];
            this.userService.getObjectById(id).then(user => {this.user = user; console.log('user',this.user);})
                .catch(error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
        }
        else
        {
            this.user = new User();
        }
    });
  }

  ngOnDestroy()
  {
    this.sub.unsubscribe();
  }

  onCancel()
  {
    this.router.navigate(['/users']);
  }
  
  onSave()
  {
      console.log('save',this.user);
      this.userService.save(this.user)
          .then((user: User) => {
                this.user = user;
                this.close.emit(user);
                this.router.navigate(['/users']);
          })
          .catch((error: JsonError) => this.msgs.push({severity:'error', summary:'Error Saving', detail:error.error})); 
        
  }
  
  onSubmit()
  {
      return this.form.valid;
      
  }

    
  groupExists(id): boolean
  {
      for (let ii = 0; ii < this.user.permissionGroupCollection.length;ii++)
      {
          if (this.user.permissionGroupCollection[ii].id == id)
            return true;
      }
      return false;
  }
  
  toggleGroup(id)
  {
      let found: boolean = false;
      for (let ii = 0; ii < this.user.permissionGroupCollection.length;ii++)
      {
          if (this.user.permissionGroupCollection[ii].id == id)
          {
              this.user.permissionGroupCollection.splice(ii,1);
              found = true;
              break;
          }
      }
      if (!found)
      {
          for (let ii = 0; ii < this.groups.length;ii++)
          {
              if (this.groups[ii].id == id)
              {
                  this.user.permissionGroupCollection.push(this.groups[ii]);
                  break;
              }
          }
          
      }
  }

  
  
}