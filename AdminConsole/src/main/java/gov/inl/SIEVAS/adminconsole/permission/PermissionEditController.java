/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.permission;

import gov.inl.SIEVAS.adminconsole.BaseEditController;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Class to handle permission edit.
 *
 * @author monejh
 */
public class PermissionEditController extends BaseEditController<Permission>
{

    @FXML
    private TextField txtID; 
    
    @FXML
    private TextField txtPermissionName;
    
    @FXML
    private TextArea txtDescription;
    
    
    @Override
    public void LoadData()
    {
        if (object.getId()!=null)
            txtID.setText(object.getId().toString());
        txtPermissionName.setText(object.getPermissionName());
        txtDescription.setText(object.getDescription());
    }

    @Override
    public String getObjectURL()
    {
        return "api/permissions/";
    }

    @Override
    public void UpdateObjectWithFormData()
    {
        object.setPermissionName(txtPermissionName.getText());
        object.setDescription(txtDescription.getText());
    }
    
    @Override
    public String getObjectTextName()
    {
        return "Permission";
    }
    
    
}
