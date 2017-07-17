import { IIdentifier } from '../IIdentifier';
import { ITypeName } from '../ITypeName';

export class Permission implements IIdentifier<number>, ITypeName
{
    id: number;
    typeName: string;
    permissionName: string;
    description: string;
    
    constructor();
    constructor(data: any);
    constructor(data?: any, id?: number, name?: string, description?: string)
    {
        if (data)
        {
            this.id = data.id;
            this.permissionName = data.permissionName;
            this.description = data.description;
        }
        else
        {
            if (id)
                this.id = id;
            if (name)
                this.permissionName = name;
            if (description)
                this.description = description;
        }
    }
}
