package gov.inl.SIEVAS.adminconsole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class AdminConsoleController //implements Initializable
{

    @FXML
    private AnchorPane content;

    private String baseURL = "";
    private final RestController restController = RestController.getInstance();
    
    private static final String ABOUT_TITLE = "SIEVAS Administration Console";
    private static final String ABOUT_TEXT = "About this";
    private static final String ABOUT_DESCRIPTION = "SIEVAS v0.1";
    private static final String PERMISSION_FXML = "/fxml/Permissions.fxml";
    protected static final String CONTENT_FAILURE_MSG = "Failure loading content";
    private static final String COLON = ":";
    
    /***
     * Sets the base URL for the server
     * @param url 
     */
    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    
    
    /***
     * Loads the data once all the parameters are set. This requires baseURL
     * and RestController to be set.
     */
    public void LoadData()
    {
       
        //nothing to do here
    }
    
    
    @FXML
    private void handleQuit(ActionEvent event)
    {
        System.exit(0);
    }
    
    @FXML
    private void handleAbout(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ABOUT_TITLE);
        alert.setHeaderText(ABOUT_TEXT);
        alert.setContentText(ABOUT_DESCRIPTION);

        alert.showAndWait();
    }
    
    @FXML
    private void handlePermissions(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PERMISSION_FXML));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG + COLON + PERMISSION_FXML , e);
            return;
        }
        setContent((Pane)root);
        
        PermissionsController controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.LoadData();
        
    }
    
    
    public void setContent (Pane anchorPane){
        List<Node> nodes = anchorPane.getChildren();
        content.getChildren().clear();
        content.getChildren().addAll(nodes);
    }
    
    
}
