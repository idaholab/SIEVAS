/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.hansolo.enzo.notification.Notification;
import eu.hansolo.enzo.notification.Notification.Notifier;
import gov.inl.SIEVAS.adminconsole.BaseEditController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.RestController;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroup;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroupEditController;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * User Edit Controller
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
    private TextField txtEDIPI;
    
    @FXML
    private CheckBox cbEnabled;
    
    @FXML
    private CheckBox cbLocked;
    
    @FXML
    private CheckBox cbExpired;
    
    @FXML
    private ListView lvGroups;
    
    private static final String PERMISSIONGROUP_URL = "api/permissiongroups/";
    private static final String USERCHECK_URL = "api/users/username/";
    private final RestController restController = RestController.getInstance();
    
    private Boolean validateEDIPI(TextField txt)
    {
        if (txtEDIPI.getText().isEmpty())
            return true;
        else if (txtEDIPI.getText().matches("^\\d+$"))
            return true;
        else
        {
            Notifier.INSTANCE.notifyError("Validation Error", "Invalid EDIPI - must be a positive number");
            return false;
        }
    }
    
    private Boolean validateFirstName(TextField txt)
    {
        if (txtFirstName.getText().isEmpty())
        {
            Notifier.INSTANCE.notifyError("Validation Error", "First Name is required.");
            return false;
        }
        else
            return true;
    }
    
    private Boolean validateLastName(TextField txt)
    {
        if (txtLastName.getText().isEmpty())
        {
            Notifier.INSTANCE.notifyError("Validation Error", "Last Name is required.");
            return false;
        }
        else        
            return true;
    }
    
    private Boolean validateUsername(TextField txt)
    {
        if (txtUsername.getText().isEmpty())
        {
            Notifier.INSTANCE.notifyError("Validation Error", "Username is required.");
            return false;
        }
        else
        {
            HttpGet request;
            long id = 0L;
            if (object.getId()!=null)
                id = object.getId();
            try
            {
                request = new HttpGet(baseURL + USERCHECK_URL + URLEncoder.encode(txtUsername.getText(), DEFAULT_ENCODING) + "/" + id);
            } 
            catch (UnsupportedEncodingException ex)
            {
                Logger.getLogger(UserEditController.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            System.out.println("URL TO GET:" + request.getURI());
            HttpResponse response;
            try
            {
                response = restController.ProcessRequest(request);
            } 
            catch (IOException ex)
            {
                Logger.getLogger(UserEditController.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
            try
            {
                EntityUtils.consume(response.getEntity());
            }
            catch (IOException ex)
            {
                Logger.getLogger(UserEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("RESULT:" + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode()==200)
            {
                Notifier.INSTANCE.notifyError("Validation Error", "Username is already used.");
                return false;
            }
            else
                return true;
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        super.initialize(url, rb);
        
        
        
        AddControlToValidate(txtUsername,(Callback<Control, Boolean>) (Control param) -> validateUsername((TextField)param));
        AddControlToValidate(txtFirstName,(Callback<Control, Boolean>) (Control param) -> validateFirstName((TextField)param));
        AddControlToValidate(txtLastName,(Callback<Control, Boolean>) (Control param) -> validateLastName((TextField)param));
        AddControlToValidate(txtEDIPI,(Callback<Control, Boolean>) (Control param) -> validateEDIPI((TextField)param));
    }
    
    @Override
    public void LoadData()
    {
        Notifier.setNotificationOwner((Stage) txtUsername.getScene().getWindow());
        
        if (object.getId()!=null)
            txtID.setText(object.getId().toString());
        txtUsername.setText(object.getUsername());
        txtFirstName.setText(object.getFirstName());
        txtLastName.setText(object.getLastName());
        if (object.getEdipi()!=null)
            txtEDIPI.setText(object.getEdipi().toString());
        else
            txtEDIPI.setText("");
        
        cbEnabled.setSelected(object.getEnabled());
        cbLocked.setSelected(object.getLocked());
        cbExpired.setSelected(object.getExpired());
        
        
        
        
        
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
        if (!txtEDIPI.getText().isEmpty())
            object.setEdipi(Long.parseLong(txtEDIPI.getText()));
        object.setEnabled(cbEnabled.isSelected());
        object.setLocked(cbLocked.isSelected());
        object.setExpired(cbExpired.isSelected());
    }
    
    @Override
    public String getObjectTextName()
    {
        return "User";
    }
    
    
}
