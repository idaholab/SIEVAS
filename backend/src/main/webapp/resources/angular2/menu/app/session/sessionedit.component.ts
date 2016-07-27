
import { Component, OnInit, OnDestroy, Input, Output, EventEmitter } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, Validators, FormGroup } from '@angular/forms';

import { Message, Growl } from 'primeng/primeng';

import 'rxjs/add/operator/do';

import { ResultSet } from '../resultset';
import { LIVESession } from './session';
import { Group } from '../group/group';
import { User } from '../user/user';
import { SessionService } from './session.service';
import { GroupService } from '../group/group.service';
import { UserService } from '../user/user.service';
import { JsonError } from '../JsonError';

@Component({
  selector: 'group-detail',
  templateUrl: 'resources/angular2/menu/app/session/sessionedit.html',
  styleUrls:  ['resources/angular2/menu/app/session/session.css'],
  providers: [ SessionService, GroupService, UserService ],
  directives: [Growl]
})
export class SessionEditComponent implements OnInit, OnDestroy
{
  @Input()
  session: LIVESession;
  
  @Output()
  close = new EventEmitter();
  
  sub: any;
  form: FormGroup;
  msgs: Message[] = [];
  groups: Group[] = [];
  users: User[] = [];
  

  constructor(
    private sessionService: SessionService,
    private groupService: GroupService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder) {
    
      this.form = this.formBuilder.group({
         'name': ['', Validators.required]
         
      });
  }

  ngOnInit()
  {
      
      this.groupService.getObjects(0, -1, "groupName", 1, null).subscribe((result: any) => {
          this.groups = result.data;
      });
      
      this.userService.getObjects(0, -1, "username", 1, null).subscribe((result: any) => {
          this.users = result.data;
      });
      
    this.sub = this.route.params.subscribe(params => {
        if (params['id'] !== undefined)
        {
            let id = +params['id'];
            this.sessionService.getObjectById(id).then(session => this.session = session)
                .catch(error => this.msgs.push({severity:'error', summary:'Error Message', detail:error}));
        }
        else
        {
            this.session = new LIVESession();
        }
    });
  }

  ngOnDestroy()
  {
    this.sub.unsubscribe();
  }

  onCancel()
  {
    this.router.navigate(['/sessions']);
  }
  
  onSave()
  {
      this.sessionService.save(this.session)
          .then((session: LIVESession) => {
                this.session = session;
                this.close.emit(session);
                this.router.navigate(['/sessions']);
          })
          .catch((error: JsonError) => this.msgs.push({severity:'error', summary:'Error Saving', detail:error.error})); 
          
  }
  
  onSubmit()
  {
      return this.form.valid;
      
  }
  
  isOwner(id)
  {
      return (this.session.owner && this.session.owner.id == id);
  }

  setOwner(id)
  {
      this.session.owner = null;
      for (let ii = 0; ii < this.users.length;ii++)
      {
          if (this.users[ii].id == id)
              this.session.owner = this.users[ii];
      }
            
  }
  
  ownerValid()
  {
      if (this.session.owner)
        return true;
      else
        return false;
  }
    
  userExists(id): boolean
  {
      for (let ii = 0; ii < this.session.users.length;ii++)
      {
          if (this.session.users[ii].id == id)
            return true;
      }
      return false;
  }
  
  toggleUser(id)
  {
      let found: boolean = false;
      for (let ii = 0; ii < this.session.users.length;ii++)
      {
          if (this.session.users[ii].id == id)
          {
              this.session.users.splice(ii,1);
              found = true;
              break;
          }
      }
      if (!found)
      {
          for (let ii = 0; ii < this.users.length;ii++)
          {
              if (this.users[ii].id== id)
              {
                  this.session.users.push(this.users[ii]);
                  break;
              }
          }
          
      }
  }
  
  groupExists(id): boolean
  {
      for (let ii = 0; ii < this.session.groups.length;ii++)
      {
          if (this.session.groups[ii].id == id)
            return true;
      }
      return false;
  }
  
  toggleGroup(id)
  {
      let found: boolean = false;
      for (let ii = 0; ii < this.session.groups.length;ii++)
      {
          if (this.session.groups[ii].id == id)
          {
              this.session.groups.splice(ii,1);
              found = true;
              break;
          }
      }
      if (!found)
      {
          for (let ii = 0; ii < this.groups.length;ii++)
          {
              if (this.groups[ii].id== id)
              {
                  this.session.groups.push(this.groups[ii]);
                  break;
              }
          }
          
      }
  }

  
  
}