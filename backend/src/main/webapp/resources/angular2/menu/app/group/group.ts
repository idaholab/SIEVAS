import { IIdentifier } from '../IIdentifier';
import { ITypeName } from '../ITypeName';
import { Permission } from '../permission/permission';

export class Group implements IIdentifier<number>, ITypeName
{
    id: number;
    typeName: string;
    groupName: string;
    description: string;
    permissionCollection: Permission[] = [];
    
    constructor();
    constructor(data: any);
    constructor(data?: any, id?: number, name?: string, description?: string)
    {
        if (data)
        {
            this.id = data.id;
            this.groupName = data.permissionName;
            this.description = data.description;
            this.permissionCollection = data.permissionCollection;
        }
        else
        {
            if (id)
                this.id = id;
            if (name)
                this.groupName = name;
            if (description)
                this.description = description;
        }
    }
}
