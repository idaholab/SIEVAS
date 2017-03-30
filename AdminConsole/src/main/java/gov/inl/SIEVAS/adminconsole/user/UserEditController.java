/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.adminconsole.BaseEditController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.RestController;
import gov.inl.SIEVAS.adminconsole.permission.Permission;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroup;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroupEditController;
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
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class UserEditController extends BaseEditController<UserInfo>
{

    @FXML
    private TextField txtID; 
    
    @FXML
    private TextField txtUsername;
    
    @FXML
    private TextField txtFirstName;
    
    @FXML
    private TextField txtLastName;
    
    @FXML
    private ListView lvGroups;
    
    private static final String PERMISSIONGROUP_URL = "api/permissiongroups/";
    private final RestController restController = RestController.getInstance();
    
    
    @Override
    public void LoadData()
    {
        if (object.getId()!=null)
            txtID.setText(object.getId().toString());
        txtUsername.setText(object.getUsername());
        txtFirstName.setText(object.getFirstName());
        txtLastName.setText(object.getLastName());
        
        HttpGet request = new HttpGet(baseURL + PERMISSIONGROUP_URL + "?start=0&count=-1");
        
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
            JsonListResult<PermissionGroup> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<PermissionGroup > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(PermissionGroupEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            lvGroups.setItems(FXCollections.observableArrayList(result.getData()));
            
            lvGroups.setCellFactory(CheckBoxListCell.forListView(new Callback<PermissionGroup, ObservableValue<Boolean>>(){
                @Override
                public ObservableValue<Boolean> call(PermissionGroup param)
                {
                    ChangeListener<? super Boolean> listener  = (observable, oldValue, newValue)->
                        {
                            if (newValue)
                                object.getPermissionGroupCollection().add(param);
                            else
                                object.getPermissionGroupCollection().remove(param);
                        };
                    
                    for(PermissionGroup group: object.getPermissionGroupCollection())
                    {
                        if (Objects.equals(group.getId(), param.getId()))
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
                
            }, new StringConverter<PermissionGroup>() {
                    @Override
                    public String toString(PermissionGroup object)
                    {
                        return object.getGroupName();
                    }

                    @Override
                    public PermissionGroup fromString(String string)
                    {
                        return null;
                    }
                }));
        }
    }
    
    
   
    @Override
    public String getObjectURL()
    {
        return "api/users/";
    }

    @Override
    public void UpdateObjectWithFormData()
    {
        object.setUsername(txtUsername.getText());
        object.setFirstName(txtFirstName.getText());
        object.setLastName(txtLastName.getText());
    }
    
    @Override
    public String getObjectTextName()
    {
        return "User";
    }
    
    
}
