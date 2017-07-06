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
package gov.inl.SIEVAS.adminconsole.datasource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.adminconsole.BaseEditController;
import gov.inl.SIEVAS.adminconsole.DriverOption;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.RadioListCell;
import gov.inl.SIEVAS.adminconsole.RestController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

/**
 * Class to handle datasource edit.
 *
 * @author monejh
 */
public class DatasourceEditController extends BaseEditController<Datasource>
{
    
    private static final String DRIVER_URL = "api/drivers/";
    private static final String DRIVER_OPTIONS_URL = "api/drivers/optionlist/";
    private static final String OPTION_PARAM_NAME = "driver";
    private static final String EQUALS = "=";
    private static final String QUERY_PARAM = "?";
    
    @FXML
    private TextField txtID; 
    
    @FXML
    private TextField txtName;
    
    
    @FXML
    private TextArea txtDescription;
    
    @FXML
    private ListView lvDrivers;
    
    @FXML
    private TableView tvOptions;
    
    @FXML
    private TableColumn<DataSourceOption,String> colOptionValue;
    
    private String originalDriver = "";
    private List<DataSourceOption> originalOptions = new ArrayList<DataSourceOption>();
    
    
    private RestController restController = RestController.getInstance();
    
    
    
    /***
     * Loads the data for the users, groups, and data sources.
     */
    @Override
    public void LoadData()
    {
        if (object.getId()!=null)
            txtID.setText(object.getId().toString());
        txtName.setText(object.getName());
        txtDescription.setText(object.getDescription());
        
        HttpGet request = new HttpGet(baseURL + DRIVER_URL + "?start=0&count=-1");
        
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
            JsonListResult<DriverInfo> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<DriverInfo > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(DatasourceEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            lvDrivers.setItems(FXCollections.observableArrayList(result.getData()));
            
            ToggleGroup group = new ToggleGroup();
            lvDrivers.setCellFactory(RadioListCell.forListView(new Callback<DriverInfo, ObservableValue<Boolean>>(){
                @Override
                public ObservableValue<Boolean> call(DriverInfo param)
                {
                    ChangeListener<? super Boolean> listener  = (observable, oldValue, newValue)->
                        {
                            if (newValue)
                            {
                                object.setDriverName(param.getFullName());
                                handleOptionChange();
                            }
                        };
                    
                    SimpleBooleanProperty prop;
                    if (param.getFullName().equals(object.getDriverName()))
                         prop = new SimpleBooleanProperty(true);
                    else
                        prop = new SimpleBooleanProperty(false);
                    
                    prop.addListener(listener);
                    return prop;
                }
                
            }, new StringConverter<DriverInfo>() {
                    @Override
                    public String toString(DriverInfo object)
                    {
                        return object.getFullName();
                    }

                    @Override
                    public DriverInfo fromString(String string)
                    {
                        return null;
                    }
                }, group));
        }
        originalOptions.clear();
        originalOptions = new ArrayList<DataSourceOption>();
        if (object.getOptions()!=null)
            for(DataSourceOption opt: object.getOptions())
                originalOptions.add(opt);
        originalDriver = object.getDriverName();
        if (originalDriver == null)
            originalDriver = "";
        
        handleOptionChange();
        
    }

    private void handleOptionChange()
    {
        
        if (object.getOptions() == null)
            object.setOptions(new ArrayList<DataSourceOption>());
        if (originalOptions == null)
            originalOptions = new ArrayList<DataSourceOption>();
        
        if (originalDriver.equals(object.getDriverName()))
        {
            object.getOptions().clear();
            if (originalOptions!=null)
                object.getOptions().addAll(originalOptions);
        }
        
        //get opts
        HttpGet request = new HttpGet(baseURL + DRIVER_OPTIONS_URL + QUERY_PARAM +  OPTION_PARAM_NAME + EQUALS + object.getDriverName());
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
            JsonListResult<DriverOption> result;
            try
            {
                result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<DriverOption > >(){});
            }
            catch (Exception ex)
            {
                Logger.getLogger(DatasourceEditController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        
            //add new options
            for(DriverOption opt: result.getData())
            {
                boolean found = false;
                for(DataSourceOption opt2: object.getOptions())
                {
                    if (opt2.getOptionName().toLowerCase().equals(opt.getOptionName().toLowerCase()))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    DataSourceOption newOpt = new DataSourceOption(null, opt.getOptionName());
                    
                    newOpt.setOptionValue(opt.getOptionValue());
                    object.getOptions().add(newOpt);
                    newOpt.setDatasource(object);
                }
            }
            //remove unneeded
            for(DataSourceOption opt: object.getOptions().toArray(new DataSourceOption[0]))
            {
                boolean found = false;
                for(DriverOption opt2: result.getData())
                {
                    if (opt2.getOptionName().toLowerCase().equals(opt.getOptionName().toLowerCase()))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                {
                    object.getOptions().remove(opt);
                }
            }
            
            
            tvOptions.getItems().removeAll(tvOptions.getItems());
            tvOptions.getItems().addAll(object.getOptions());
            colOptionValue.setOnEditCommit(event -> event.getRowValue().setOptionValue(event.getNewValue()));
        }
    }
    @Override
    public String getObjectURL()
    {
        return "api/datasources/";
    }

    @Override
    public void UpdateObjectWithFormData()
    {
        object.setName(txtName.getText());
        object.setDescription(txtDescription.getText());
    }
    
    @Override
    public String getObjectTextName()
    {
        return "Datasource";
    }
}
