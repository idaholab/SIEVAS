/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
 import { ITypeName } from './ITypeName';

export class JsonError implements ITypeName
{
    typeName: string;
    error: string;
    
    constructor(error: string)
    {
        this.error = error;
    }
    
}

