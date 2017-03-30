/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author monejh
 * @param <T>
 */
public abstract class BaseEditController<T extends IIdentifier> implements Initializable
{
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_TYPE_JSON = "application/json";
    private static final String DEFAULT_ENCODING = "UTF-8";
    protected final String DATA_SAVING_ERROR_MSG = "Save " + getObjectTextName() + " error";
    private static final String COLON = ":";
    
    
    private String baseURL = "";
    private final RestController restController = RestController.getInstance();
    protected T object;
    private boolean createMode = false;
    
    @FXML
    private Button btnCancel;
    
    @FXML
    private Button btnSave;
    
    
    /* Need to override these*/
    public abstract void LoadData();
    public abstract String getObjectURL();
    public abstract void UpdateObjectWithFormData();
    public abstract String getObjectTextName();
    
   
    /***
     * Sets the base URL for the server
     * @param url 
     */
    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    
    
    public void setObject(T obj)
    {
        this.object = obj;
    }
    
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
        // TODO
    }
    
    
    
    
    private boolean SaveData()
    {
        String url = baseURL + getObjectURL();
        if (!createMode)
            url += object.getId();
        
        
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
    
    
    
    @FXML
    private void onCancel(ActionEvent event)
    {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    
    
    
    @FXML
    private void onSave(ActionEvent event)
    {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        UpdateObjectWithFormData();
        boolean result = SaveData();
        if (result)
            stage.close();
    }
}
