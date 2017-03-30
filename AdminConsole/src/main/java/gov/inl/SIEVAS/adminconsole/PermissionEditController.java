/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
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
    
    
    /*
    public static String permissionListURL = "api/permissions/";
    
    private String baseURL = "";
    private RestController restController = RestController.getInstance();
    private Permission permission;
    private boolean createMode = false;
    
    @FXML
    private TextField txtID; 
    
    @FXML
    private TextField txtPermissionName;
    
    @FXML
    private TextArea txtDescription;
    
    @FXML
    private Button btnCancel;
    
    @FXML
    private Button btnSave;
    
   
    
    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    public void setRestController(RestController rest)
    {
        this.restController = rest;
    }
    
    public void setPermission(Permission perm)
    {
        this.permission = perm;
    }
    
    public void setCreateMode(boolean create)
    {
        this.createMode = create;
    }
    

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }
    
    public void LoadData()
    {
        System.out.println(permission);
        if (permission.getId()!=null)
            txtID.setText(permission.getId().toString());
        txtPermissionName.setText(permission.getPermissionName());
        txtDescription.setText(permission.getDescription());
    }
    
    private boolean SaveData()
    {
        String url = baseURL + permissionListURL;
        if (!createMode)
            url += permission.getId();
        
        System.out.println("URL:" + url);
        System.out.println("perm:" + permission);
        
        
        HttpEntityEnclosingRequestBase request;
        if (createMode)
            request = new HttpPost(url);
        else
            request = new HttpPut(url);
        System.out.println(request);
        request.setHeader("Content-Type", "application/json");
        ObjectMapper objMapper = new ObjectMapper();
        try
        {
            System.out.println("SENDING ENTITY:" + objMapper.writeValueAsString(permission));
            request.setEntity(new StringEntity(objMapper.writeValueAsString(permission), "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(PermissionEditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (JsonProcessingException ex)
        {
            Logger.getLogger(PermissionEditController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        HttpResponse response;
        try
        {
             response = restController.ProcessRequest(request);
        }
        catch (IOException ex)
        {
            Logger.getLogger(PermissionEditController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        if (response.getStatusLine().getStatusCode()!=200)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data saving error");
            alert.setHeaderText("Save permissions error");

            try
            {
                JsonError error = objMapper.readValue(response.getEntity().getContent(), JsonError.class);
                alert.setContentText(response.getStatusLine().getReasonPhrase() + ":" + error.getError());
            }
            catch (Exception ex)
            {
                Logger.getLogger(PermissionEditController.class.getName()).log(Level.SEVERE, null, ex);
            }
            alert.showAndWait();
            
            return false;

        }
        try
        {
            response.getEntity().consumeContent();
        }
        catch (IOException ex)
        {
            Logger.getLogger(PermissionEditController.class.getName()).log(Level.SEVERE, null, ex);
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
        permission.setPermissionName(txtPermissionName.getText());
        permission.setDescription(txtDescription.getText());
        boolean result = SaveData();
        if (result)
            stage.close();
    }*/

    
    
    
}
