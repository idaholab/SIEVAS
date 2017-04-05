/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 *
 * @author monejh
 */
public class CheckBoxTableCellFactory<S, T> 
        implements Callback<TableColumn<S, T>, TableCell<S, T>> 
{
    public TableCell<S, T> call(TableColumn<S, T> param)
    {
        
        return new CheckBoxTableCell<S,T>();
    }
}
