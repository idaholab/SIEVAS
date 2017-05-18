package gov.inl.SIEVAS.adminconsole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import gov.inl.SIEVAS.adminconsole.datasource.DatasourcesController;
import gov.inl.SIEVAS.adminconsole.permission.PermissionsController;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroupController;
import gov.inl.SIEVAS.adminconsole.session.SessionsController;
import gov.inl.SIEVAS.adminconsole.user.UserController;
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
 * Class to handle the main window.
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
    private static final String PERMISSIONGROUP_FXML = "/fxml/PermissionGroup.fxml";
    private static final String USER_FXML = "/fxml/User.fxml";
    private static final String DATASOURCE_FXML = "/fxml/Datasources.fxml";
    private static final String SESSION_FXML = "/fxml/Sessions.fxml";
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
    
    /***
     * Handle the quit menu item by exiting
     * @param event The event to process
     */
    @FXML
    private void handleQuit(ActionEvent event)
    {
        System.exit(0);
    }
    
    
    /***
     * Handles the about dialog.
     * @param event The event to process
     */
    @FXML
    private void handleAbout(ActionEvent event)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(ABOUT_TITLE);
        alert.setHeaderText(ABOUT_TEXT);
        alert.setContentText(ABOUT_DESCRIPTION);

        alert.showAndWait();
    }
    
    /***
     * Handles the permission admin selection by showing the pane for the grid.
     * @param event The event to process
     */
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
            Logger.getLogger(AdminConsoleController.class.getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG + COLON + PERMISSION_FXML , e);
            return;
        }
        setContent((Pane)root);
        
        PermissionsController controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.LoadData();
        
    }
    
    
    /***
     * Handles the groups admin selection by showing the pane for the grid.
     * @param event The event to process
     */
    @FXML
    private void handlePermissionGroups(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PERMISSIONGROUP_FXML));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(AdminConsoleController.class.getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG + COLON + PERMISSION_FXML , e);
            return;
        }
        setContent((Pane)root);
        
        PermissionGroupController controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.LoadData();
        
    }
    
    
    /***
     * Handles the users selection by showing the pane for the grid.
     * @param event The event to process
     */
    @FXML
    private void handleUsers(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(USER_FXML));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(AdminConsoleController.class.getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG + COLON + PERMISSION_FXML , e);
            return;
        }
        setContent((Pane)root);
        
        UserController controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.LoadData();
        
    }
    
     /***
     * Handles the datasources selection by showing the pane for the grid.
     * @param event The event to process
     */
    @FXML
    private void handleDatasources(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(DATASOURCE_FXML));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(AdminConsoleController.class.getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG + COLON + DATASOURCE_FXML , e);
            return;
        }
        setContent((Pane)root);
        
        DatasourcesController controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.LoadData();
        
    }
    
    /***
     * Handles the sessions selection by showing the pane for the grid.
     * @param event The event to process
     */
    @FXML
    private void handleSessions(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SESSION_FXML));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG + COLON + SESSION_FXML , e);
            return;
        }
        setContent((Pane)root);
        
        SessionsController controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.LoadData();
        
    }
    
    /***
     * Sets the content of the main window pane replace what is there.
     * @param anchorPane The new content of the pane.
     */
    public void setContent (Pane anchorPane)
    {
        List<Node> nodes = anchorPane.getChildren();
        content.getChildren().clear();
        content.getChildren().addAll(nodes);
    }
    
    
}
