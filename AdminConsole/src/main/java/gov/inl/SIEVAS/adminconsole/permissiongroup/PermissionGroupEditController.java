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
package gov.inl.SIEVAS.adminconsole.permissiongroup;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.adminconsole.BaseEditController;
import gov.inl.SIEVAS.adminconsole.RestController;
import gov.inl.SIEVAS.common.JsonListResult;
import gov.inl.SIEVAS.entity.Permission;
import gov.inl.SIEVAS.entity.PermissionGroup;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

/**
 * Class to handle permission edit.
 *
 * @author monejh
 */
public class PermissionGroupEditController extends BaseEditController<PermissionGroup>
{

    @FXML
    private TextField txtID; 
    
    @FXML
    private TextField txtGroupName;
    
    @FXML
    private TextArea txtDescription;
    
    @FXML
    private ListView lvPermissions;
    
    
    private static final String PERMISSION_URL = "api/permissions/";
    
    private RestController restController = RestController.getInstance();
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        
    }
    
    
    
    @Override
    public void LoadData()
    {
        if (object.getId()!=null)
            txtID.setText(object.getId().toString());
        txtGroupName.setText(object.getGroupName());
        txtDescription.setText(object.getDescription());
        
        HttpGet request = new HttpGet(baseURL + PERMISSION_URL + "?start=0&count=-1");
        
        HttpResponse response = null;
        try
        {
             response = restController.ProcessRequest(request);
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return;
        }
        if (response.getStatusLine().getStatusCode()==200)
        {
            ObjectMapper objMapper = new ObjectMapper();
            //read result as a JsonListResult<SIEVASSession>
            //NOTE: users and groups are missing in the local object since it is not needed
            /*StringWriter writer = new StringWriter();
            String txt = IOUtils.toString(response.getEntity().getContent());*/
            JsonListResult<Permission> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<Permission > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(PermissionGroupEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            lvPermissions.setItems(FXCollections.observableArrayList(result.getData()));
            
            lvPermissions.setCellFactory(CheckBoxListCell.forListView(new Callback<Permission, ObservableValue<Boolean>>(){
                @Override
                public ObservableValue<Boolean> call(Permission param)
                {
                    ChangeListener<? super Boolean> listener  = (observable, oldValue, newValue)->
                        {
                            if (newValue)
                                object.getPermissionCollection().add(param);
                            else
                                object.getPermissionCollection().remove(param);
                        };
                    
                    for(Permission perm: object.getPermissionCollection())
                    {
                        if (Objects.equals(perm.getId(), param.getId()))
                        {
                            SimpleBooleanProperty prop = new SimpleBooleanProperty(true);
                            prop.addListener(listener);
                            return prop;
                        }
                    }
                    SimpleBooleanProperty prop = new SimpleBooleanProperty(false);
                    prop.addListener(listener);
                    return prop;
                }
                
            }, new StringConverter<Permission>() {
                    @Override
                    public String toString(Permission object)
                    {
                        return object.getPermissionName();
                    }

                    @Override
                    public Permission fromString(String string)
                    {
                        return null;
                    }
                }));
        }
    }

    @Override
    public String getObjectURL()
    {
        return "api/permissiongroups/";
    }

    @Override
    public void UpdateObjectWithFormData()
    {
        object.setGroupName(txtGroupName.getText());
        object.setDescription(txtDescription.getText());
    }
    
    @Override
    public String getObjectTextName()
    {
        return "Group";
    }
    
    
}
