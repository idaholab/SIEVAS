/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
import { FormBuilder, Validators, FormGroup, FormControl, NG_VALIDATORS, AbstractControl } from '@angular/forms';
import { AsyncValidatorFn, ValidatorFn, Validator } from '@angular/forms';
 import { Control } from '@angular/common';
 import { Directive } from '@angular/core';
 import {forwardRef} from '@angular/core';



export interface ValidationResult
{
 [key:string]:boolean;
}


@Directive({
  selector: '[validLong][ngModel],[validateLong][formControl]',
  providers: [
      { provide: NG_VALIDATORS, useValue: LongValidator.validLong, multi: true }
  ]
})
export class LongValidator implements Validator
{
    private _validator: ValidatorFn;
    
    constructor()
    {
        this._validator = LongValidator.validLong;
    }
    
    validate(c: AbstractControl) { return this._validator(c);}
    
    static isInt(n: string): boolean
    {
        return +n == parseInt(n) && /^\d+$/.test(n);
    }    
    
    
    static validLong(c: AbstractControl) : { [key: string]: any}
    {
        console.log('validLong', c.value,LongValidator.isInt(c.value));
        if (c.value != "" && LongValidator.isInt(c.value))
            return null;
        else if (c.value == "" || c.value == null)
            return null;
        else
            return { "validLong": true };
    }
    
    
}

@Directive({
  selector: '[validFloat][ngModel],[validateFloat][formControl]',
  providers: [
      { provide: NG_VALIDATORS, useValue: FloatValidator.validFloat, multi: true }
  ]
})
export class FloatValidator implements Validator
{
    private _validator: ValidatorFn;
    
    constructor()
    {
        this._validator = FloatValidator.validFloat;
    }
    
    validate(c: AbstractControl) { return this._validator(c);}
    
    static isFloat(n): boolean
    {
        return n == parseFloat(n);
    }
    
    static validFloat(control: AbstractControl): {[key: string]: any;}
    {
        if (control.value != "" && FloatValidator.isFloat(control.value))
            return null;
        else if (control.value == "")
            return null;
        else
            return { "validFloat": true };
    }
}

@Directive({
  selector: '[validNumber][ngModel],[validateNumber][formControl]',
  providers: [
      { provide: NG_VALIDATORS, useValue: NumberValidator.validNumber, multi: true }
  ]
})
export class NumberValidator implements Validator
{
    private _validator: ValidatorFn;
    
    constructor()
    {
        this._validator = NumberValidator.validNumber;
    }
    
    validate(c: AbstractControl) { return this._validator(c);}
    
    static validNumber(control: AbstractControl): {[key: string]: any;}
    {
        if (control.value != "" && !isNaN(control.value))
            return null;
        else if (control.value == "")
            return null;
        else
            return { "validNumber": true };
        
    }
}
