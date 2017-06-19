/* 
 * Copyright 2017 Idaho National Laboratory.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
