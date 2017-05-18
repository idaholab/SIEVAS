/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.session;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.adminconsole.BaseEditController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.RadioListCell;
import gov.inl.SIEVAS.adminconsole.RestController;
import gov.inl.SIEVAS.adminconsole.SIEVASSession;
import gov.inl.SIEVAS.adminconsole.datasource.Datasource;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroup;
import gov.inl.SIEVAS.adminconsole.user.UserInfo;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
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
public class SessionEditController extends BaseEditController<SIEVASSession>
{
    
    private static final String USERS_URL = "api/users/";
    private static final String GROUPS_URL = "api/permissiongroups/";
    private static final String DATASOURCES_URL = "api/datasources/";
    
    @FXML
    private TextField txtID; 
    
    @FXML
    private TextField txtName;
    
    @FXML
    private TextField txtControlStreamName;
    
    @FXML
    private TextField txtDataStreamName;
    
    
    @FXML
    private ListView lvOwner;
    
    @FXML
    private ListView lvGroups;
    
    @FXML
    private ListView lvUsers;
    
    @FXML
    private ListView lvDatasources;
    
    
    private RestController restController = RestController.getInstance();
    
    
    
    @Override
    public void LoadData()
    {
        if (object.getId()!=null)
            txtID.setText(object.getId().toString());
        txtName.setText(object.getName());
        txtControlStreamName.setText(object.getControlStreamName());
        txtDataStreamName.setText(object.getDataStreamName());
        
        //handle owner
        HttpGet request = new HttpGet(baseURL + USERS_URL + "?start=0&count=-1");
        
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
            JsonListResult<UserInfo> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<UserInfo > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(SessionEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            lvOwner.setItems(FXCollections.observableArrayList(result.getData()));
            
            ToggleGroup group = new ToggleGroup();
            lvOwner.setCellFactory(RadioListCell.forListView(new Callback<UserInfo, ObservableValue<Boolean>>(){
                @Override
                public ObservableValue<Boolean> call(UserInfo param)
                {
                    ChangeListener<? super Boolean> listener  = (observable, oldValue, newValue)->
                        {
                            if (newValue)
                                object.setOwner(param);
                        };
                    
                    SimpleBooleanProperty prop;
                    if ((object.getOwner()!=null) && (param.getId() == object.getOwner().getId()))
                         prop = new SimpleBooleanProperty(true);
                    else
                        prop = new SimpleBooleanProperty(false);
                    
                    prop.addListener(listener);
                    return prop;
                }
                
            }, new StringConverter<UserInfo>() {
                    @Override
                    public String toString(UserInfo object)
                    {
                        return object.getUsername();
                    }

                    @Override
                    public UserInfo fromString(String string)
                    {
                        return null;
                    }
                }, group));
        }
        
        //handle groups
        request = new HttpGet(baseURL + GROUPS_URL + "?start=0&count=-1");
        response = null;
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
                Logger.getLogger(SessionEditController.class.getName()).log(Level.SEVERE, null, ex);
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
                                object.getGroups().add(param);
                            else
                                object.getGroups().remove(param);
                        };
                    
                    for(PermissionGroup group: object.getGroups())
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
        
        //handle users
        request = new HttpGet(baseURL + USERS_URL + "?start=0&count=-1");
        response = null;
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
            JsonListResult<UserInfo> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<UserInfo > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(SessionEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            lvUsers.setItems(FXCollections.observableArrayList(result.getData()));
            
            lvUsers.setCellFactory(CheckBoxListCell.forListView(new Callback<UserInfo, ObservableValue<Boolean>>(){
                @Override
                public ObservableValue<Boolean> call(UserInfo param)
                {
                    ChangeListener<? super Boolean> listener  = (observable, oldValue, newValue)->
                        {
                            if (newValue)
                                object.getUsers().add(param);
                            else
                                object.getUsers().remove(param);
                        };
                    
                    for(UserInfo user: object.getUsers())
                    {
                        if (Objects.equals(user.getId(), param.getId()))
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
                
            }, new StringConverter<UserInfo>() {
                    @Override
                    public String toString(UserInfo object)
                    {
                        return object.getUsername();
                    }

                    @Override
                    public UserInfo fromString(String string)
                    {
                        return null;
                    }
                }));
        }
        
        //handle datasources
        request = new HttpGet(baseURL + DATASOURCES_URL + "?start=0&count=-1");
        response = null;
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
            JsonListResult<Datasource> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<Datasource > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(SessionEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            lvDatasources.setItems(FXCollections.observableArrayList(result.getData()));
            
            lvDatasources.setCellFactory(CheckBoxListCell.forListView(new Callback<Datasource, ObservableValue<Boolean>>(){
                @Override
                public ObservableValue<Boolean> call(Datasource param)
                {
                    ChangeListener<? super Boolean> listener  = (observable, oldValue, newValue)->
                        {
                            if (newValue)
                                object.getDatasources().add(param);
                            else
                                object.getDatasources().remove(param);
                        };
                    
                    for(Datasource ds: object.getDatasources())
                    {
                        if (Objects.equals(ds.getId(), param.getId()))
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
                
            }, new StringConverter<Datasource>() {
                    @Override
                    public String toString(Datasource object)
                    {
                        return object.getName();
                    }

                    @Override
                    public Datasource fromString(String string)
                    {
                        return null;
                    }
                }));
        }
    }

    @Override
    public String getObjectURL()
    {
        return "api/sessions/";
    }

    @Override
    public void UpdateObjectWithFormData()
    {
        object.setName(txtName.getText());
    }
    
    @Override
    public String getObjectTextName()
    {
        return "Session";
    }
}
