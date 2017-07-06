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
package gov.inl.SIEVAS.adminconsole.user;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.inl.SIEVAS.adminconsole.BaseController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroup;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class UserController extends BaseController<UserInfo>//implements Initializable
{
    @FXML
    private TableColumn colEnabled;
    
    @FXML
    private TableColumn colLocked;
    
    @FXML
    private TableColumn colExpired;
    
    @Override
    public void initialize()
    {
        super.initialize();
        Callback<TableColumn<UserInfo, Boolean>, TableCell<UserInfo, Boolean>> cellFactory = new Callback<TableColumn<UserInfo, Boolean>, TableCell<UserInfo, Boolean>>() {

            public TableCell<UserInfo, Boolean> call(TableColumn<UserInfo, Boolean> p) {

                return new CheckBoxTableCell<UserInfo, Boolean>(new Callback<Integer, ObservableValue<Boolean>>(){
                    @Override
                    public ObservableValue<Boolean> call(Integer param)
                    {
                        
                    return new SimpleBooleanProperty(p.getCellData(param).booleanValue());
                    }
                    
                });
                }
            };
        colEnabled.setCellFactory(cellFactory);
        colExpired.setCellFactory(cellFactory);
        colLocked.setCellFactory(cellFactory);

            

        
    }
    

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/UserEdit.fxml";
    }

    @Override
    public UserInfo createObject()
    {
        UserInfo user = new UserInfo();
        user.setFirstName("");
        user.setLastName("");
        user.setEnabled(true);
        user.setPassword("");
        user.setPassword2("");
        user.setUsername("");
        user.setPermissionGroupCollection(new ArrayList<PermissionGroup>());
        
        return user;
    }

    @Override
    public String getObjectTextName()
    {
        return "user";
    }

    @Override
    public String getObjectURL()
    {
        return "api/users/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<UserInfo>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<UserInfo > >(){};
    }
    
    
    
    
    
    
}