/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole;

import javafx.fxml.FXML;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 *
 * @author monejh
 */
public class PermissionTableController
{
    public static String permissionListURL = "api/permissions/";
    private String baseURL = "";
    private RestController restController;
    
    @FXML
    private TableView tableView;
    
    @FXML
    private AnchorPane pane;
    
    
    public TableView getTableView()
    {
        return tableView;
        
    }
    
    public Pane getPane()
    {
        return pane;
    }
    
    /***
     * Sets the base URL for the server
     * @param url 
     */
    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    /***
     * Sets the REST Controller for sharing login context
     * @param rest 
     */
    public void setRestController(RestController rest)
    {
        this.restController = rest;
    }
}
