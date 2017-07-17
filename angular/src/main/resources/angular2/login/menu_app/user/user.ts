import { IIdentifier } from '../IIdentifier';
import { ITypeName } from '../ITypeName';
import { Group } from '../group/group';

export class User implements IIdentifier<number>, ITypeName
{
    id: number;
    typeName: string;
    username: string;
    password: string;
    password2: string;
    firstName: string;
    lastName: string;
    edipi: number;
    expired: boolean;
    locked: boolean;
    enabled: boolean;
    permissionGroupCollection: Group[] = [];
    
    constructor();
    constructor(data: any);
    constructor(data?: any, id?: number, username?: string, password?: string)
    {
        if (data)
        {
            this.id = data.id;
            this.username = data.username;
            this.password = data.password;
            this.password2 = data.password2;
            this.firstName = data.firstName;
            this.lastName = data.lastName;
            this.edipi = data.edipi;
            this.expired = data.expired;
            this.locked = data.locked;
            this.enabled = data.enabled;
            
            this.permissionGroupCollection = data.permissionGroupCollection;
        }
        else
        {
            if (id)
                this.id = id;
            if (username)
                this.username = name;
            if (password)
                this.password = password;
        }
    }
}
