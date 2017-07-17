import { IIdentifier } from '../IIdentifier';
import { ITypeName } from '../ITypeName';
import { User } from '../user/user';
import { Group } from '../group/group';


export class SIEVASSession implements IIdentifier<number>, ITypeName
{
    id: number;
    name: string;
    owner: User = null;
    users: User[] = [];
    groups: Group[] = [];
    typeName: string;
    dataStreamName: string;
    controlStreamName: string;
    
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
            if (data.controlStreamName)
                this.controlStreamName = data.controlStreamName;
            if (data.dataStreamName)
                this.dataStreamName = data.dataStreamName;
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
