import { IIdentifier } from '../IIdentifier';
import { ITypeName } from '../ITypeName';
import { User } from '../user/user';
import { Group } from '../group/group';


export class LIVESession implements IIdentifier<number>, ITypeName
{
    id: number;
    name: string;
    owner: User = null;
    users: User[] = [];
    groups: Group[] = [];
    typeName: string;
    
    constructor();
    constructor(data: any);
    constructor(data?: any, id?: number, name?: string, owner?: User)
    {
        if (data)
        {
            this.id = data.id;
            this.name = data.name;
            if (data.owner)
                this.owner = data.owner;
            if (data.users)
                this.users = data.users;
            if (data.groups)
                this.groups = data.groups;
        }
        else
        {
            if (id)
                this.id = id;
            if (name)
                this.name = name;
            if (owner)
                this.owner = owner;
        }
    }
}
