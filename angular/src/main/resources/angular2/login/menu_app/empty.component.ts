import { Component, OnInit, OnDestroy } from '@angular/core';







@Component({
    selector: 'empty',
    template: '<h3>Select a menu item above.</h3>',
})
export class EmptyComponent implements OnInit
{
    title = 'Main Menu';
          
    ngOnInit()
    {
        
    }
    
}


