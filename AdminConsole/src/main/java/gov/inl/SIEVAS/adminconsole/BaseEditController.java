/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 * Class to handle the edit window.
 * @author monejh
 * @param <T>
 */
public abstract class BaseEditController<T extends IIdentifier> implements Initializable
{
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    protected static final String DEFAULT_ENCODING = "UTF-8";
    protected final String DATA_SAVING_ERROR_MSG = "Save " + getObjectTextName() + " error";
    private static final String COLON = ":";
    
    
    protected String baseURL = "";
    private final RestController restController = RestController.getInstance();
    protected T object;
    private boolean createMode = false;
    
    private HashMap<Control,Boolean> validatedFields = new HashMap<>();
    private HashMap<Control,Callback<Control,Boolean>> validationFunctions = new HashMap<>();
    private List<Control> fieldsToValidate = new ArrayList<Control>();
    private HashMap<Control,ChangeListener<Boolean>> changeListeners = new HashMap<>();
    
    /* Need these two buttons */
    @FXML
    private Button btnCancel;
    
    @FXML
    private Button btnSave;
    
    
    /* Need to override these*/
    /***
     * Handles the loading of data into the window. Should take this.object 
     *  and put in the form fields.
     */
    public abstract void LoadData();
    
    /***
     * Gets the URL for saving/creating the object
     * @return The object URL relative to the baseURL
     */
    public abstract String getObjectURL();
    
    /***
     * Updates this.object with values from the form fields.
     * @return True if updated successfully, false otherwise. False typically 
     *          means validation failed.
     */
    public abstract void UpdateObjectWithFormData();
    
    /***
     * Gets the text name for the object to display to the user.
     * @return The text of the object name.
     */
    public abstract String getObjectTextName();
    
   
    /***
     * Sets the base URL for the server
     * @param url 
     */
    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    
    /**
     * Sets the object for the edit window to use.
     * @param obj 
     */
    public void setObject(T obj)
    {
        this.object = obj;
    }
    
    /***
     * Sets whether to run in create mode.
     * @param create True if in create mode, false if in edit.
     */
    public void setCreateMode(boolean create)
    {
        this.createMode = create;
    }
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        
    }
    
    
    /***
     * Saves the current data in the window
     * @return Returns true if save was successful, false otherwise.
     */
    private boolean SaveData()
    {
        String url = baseURL + getObjectURL();
        try
        {
            if (!createMode)
                url += URLEncoder.encode(object.getId().toString(),DEFAULT_ENCODING);
        }
        catch(UnsupportedEncodingException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_SAVING_ERROR_MSG);
            alert.setHeaderText(DATA_SAVING_ERROR_MSG);

            alert.setContentText(e.getMessage());
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            
            alert.showAndWait();
            return false;
        }
        
        
        HttpEntityEnclosingRequestBase request;
        if (createMode)
            request = new HttpPost(url);
        else
            request = new HttpPut(url);
        
        request.setHeader(CONTENT_TYPE_HEADER, CONTENT_TYPE_JSON);
        ObjectMapper objMapper = new ObjectMapper();
        try
        {
            request.setEntity(new StringEntity(objMapper.writeValueAsString(object), DEFAULT_ENCODING));
        }
        catch (JsonProcessingException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        
        HttpResponse response;
        try
        {
             response = restController.ProcessRequest(request);
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        if (response.getStatusLine().getStatusCode()!=200)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_SAVING_ERROR_MSG);
            alert.setHeaderText(DATA_SAVING_ERROR_MSG);

            try
            {
                JsonError error = objMapper.readValue(response.getEntity().getContent(), JsonError.class);
                alert.setContentText(response.getStatusLine().getReasonPhrase() + COLON + error.getError());
            }
            catch (Exception ex)
            {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
            alert.showAndWait();
            
            return false;

        }
        try
        {
            EntityUtils.consume(response.getEntity());
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
        
    }
    
    
    /***
     * Handles the cancel button click by closing the window.
     * @param event The event to process.
     */
    @FXML
    private void onCancel(ActionEvent event)
    {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    
    
    /***
     * Handles the save button click by saving the data.
     * @param event The event to process.
     */
    @FXML
    private void onSave(ActionEvent event)
    {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        boolean result = validateFields();
        if (!result)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Check fields");
            alert.setContentText("Please check all fields before submitting!");
            alert.showAndWait();

            return;
        }
        UpdateObjectWithFormData();
        if (result)
        {
            result = SaveData();
            if (result)
                stage.close();
            else
                return;
        }
        else
            return;
        
    }
    
    public void AddControlToValidate(Control control, Callback<Control,Boolean> callback)
    {
        validatedFields.put(control, Boolean.FALSE);
        validationFunctions.put(control, callback);
        fieldsToValidate.add(control);
        
        ChangeListener<Boolean> listener = new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue)
            {
                if (!newValue) //lost focus
                    validatedFields.put(control, callback.call(control));
            }
            
        };
        control.focusedProperty().addListener(listener);
        changeListeners.put(control, listener);
    }
    
    public void RemoveControlToValidate(Control control)
    {
        if (validatedFields.containsKey(control))
            validatedFields.remove(control);
        if (validationFunctions.containsKey(control))
            validationFunctions.remove(control);
        fieldsToValidate.remove(control);
        
        if (changeListeners.containsKey(control))
        {
            control.focusedProperty().removeListener(changeListeners.get(control));
            changeListeners.remove(control);
        }
    }
    
    protected boolean validateFields()
    {
        //first validate fields
        boolean valid = true;
        for(Control field: fieldsToValidate)
        {
            if (validatedFields.containsKey(field))
            {
                if (validatedFields.get(field) == Boolean.FALSE)
                {
                    if (validationFunctions.containsKey(field))
                    {
                        Callback<Control,Boolean> callback = validationFunctions.get(field);
                        if (callback!=null)
                        {
                            boolean result = callback.call(field);
                            validatedFields.put(field,result);
                            if (!result)
                                valid = false;
                        }
                            
                    }
                    else
                        valid = false;
                }
            }
            else
                valid = false;
        }
        return valid;
    }
}
