export class Permission
{
    id: number;
    permissionName: string;
    description: string;
    
    constructor();
    constructor(id?: number, name?: string, description?: string)
    {
        if (id)
            this.id = id;
        if (name)
            this.permissionName = name;
        if (description)
            this.description = description;
    }
}
