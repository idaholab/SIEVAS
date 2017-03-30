package gov.inl.SIEVAS.adminconsole;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.javafx.binding.LongConstant;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.SortType;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class PermissionsController extends BaseController<Permission>//implements Initializable
{

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/PermissionEdit.fxml";
    }

    @Override
    public Permission createObject()
    {
        return new Permission();
    }

    @Override
    public String getObjectTextName()
    {
        return "permission";
    }

    @Override
    public String getObjectURL()
    {
        return "api/permissions/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<Permission>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<Permission > >(){};
    }
    
    
    /*@FXML
    private Pagination pagination;
    
    @FXML
    private TableView tableView;
    
    @FXML
    private Button btnNew;
    
    @FXML
    private TableColumn<Permission, Long> colID;
    
    @FXML
    private TableColumn<Permission,String> colPermissionName;
    
    private static final String permissionListURL = "api/permissions/";
    private static final String DEFAULT_SORT_FIELD = "id";    
    
    private String baseURL = "";
    private RestController restController = RestController.getInstance();
    private Pane pane;
    private String sortField = DEFAULT_SORT_FIELD;
    private int sortOrder = 1;
    private long lastStartIndex = 0;
    

    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    
    @FXML
    public void initialize()//URL url, ResourceBundle rb)
    {
        // TODO
        
    
        pagination.setPageFactory(new Callback<Integer, Node>()
        {
            @Override
            public Node call(Integer pageIndex)
            {
                return createPage(pageIndex);
            }
        });
        
        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if ((event.getClickCount() == 2) && (!row.isEmpty()))
                {
                    this.handleMouseClick((Permission)row.getItem());
                }
            });
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    Permission perm = (Permission)row.getItem();
                    handleDelete(perm);
                }
            });
            ContextMenu menu = new ContextMenu(deleteItem);
            row.setContextMenu(menu);
            return row;
        });
        
    }
    
    
    private void handleDelete(Permission permission)
    {
        String url = baseURL + permissionListURL + permission.getId();
        
        HttpDelete request = new HttpDelete(url);
        HttpClient client = restController.getClient();
        HttpResponse response = null;
        try
        {
            response = restController.ProcessRequest(request);
        }
        catch(IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data I/O error");
            alert.setHeaderText("Save permissions error");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return;
        }
        if (response.getStatusLine().getStatusCode()!=200)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data deleting error");
            alert.setHeaderText("Delete permissions error");
            alert.setContentText(response.getStatusLine().getReasonPhrase());

            alert.showAndWait();
        }
        try
        {
            response.getEntity().consumeContent();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(PermissionsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        LoadData(lastStartIndex,PAGE_SIZE);
    }
    
    private void handleSortChange()
    {
        ObservableList<TableColumn<Permission,?>> list = tableView.getSortOrder();
        sortField = "id";
        int count = 0;
        for(TableColumn<Permission,?> col: list)
        {
            sortField = col.getId();
            if (col.getSortType() == SortType.ASCENDING)
                sortOrder = 1;
            else
                sortOrder = -1;
            System.out.println("NEW SORT(" + ++count + "):" + sortField + "," + sortOrder);
        }
        System.out.println("NEW SORT:" + sortField + "," + sortOrder);
        LoadData(lastStartIndex,PAGE_SIZE);
    }
    
    private void handleMouseClick(Permission perm)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PermissionEdit.fxml"));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,"Failure loading permissions content", e);
            return;
        }
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        Stage stage = new Stage();
        stage.setTitle("Permission Edit");
        stage.setScene(scene);
        
        
        PermissionEditController controller = loader.getController();
        controller.setRestController(restController);
        controller.setBaseURL(baseURL);
        controller.setPermission(perm);
        controller.setCreateMode(false);
        controller.LoadData();
        stage.showAndWait();
        LoadData(pagination.getCurrentPageIndex()*PAGE_SIZE,PAGE_SIZE);
        
        
    }
    
    @FXML
    private void onNewClick()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PermissionEdit.fxml"));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,"Failure loading permissions content", e);
            return;
        }
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        Stage stage = new Stage();
        stage.setTitle("Permission Create");
        stage.setScene(scene);
        
        Permission perm = new Permission();
        PermissionEditController controller = loader.getController();
        controller.setRestController(restController);
        controller.setBaseURL(baseURL);
        controller.setPermission(perm);
        controller.setCreateMode(true);
        controller.LoadData();
        stage.showAndWait();
        LoadData(pagination.getCurrentPageIndex()*PAGE_SIZE,PAGE_SIZE);
        
        
    }
    
    private int PAGE_SIZE=10;
    
    public Pane createPage(int pageIndex)
    {
        
        long start = pageIndex*PAGE_SIZE;
        lastStartIndex = start;
        System.out.println("START:" + start);
        LoadData(start, PAGE_SIZE);
        VBox box = new VBox(5.0, btnNew, tableView);
        box.setPadding(new Insets(5.0));
        return box;
    }
    
    public void LoadData()
    {
        tableView.setSortPolicy(new Callback<TableView, Boolean>(){
            @Override
            public Boolean call(TableView param)
            {
                //LoadData(lastStartIndex,PAGE_SIZE);
                handleSortChange();
                return true;
            }
            
        });
        LoadData(0,PAGE_SIZE);
    }
    
    public void LoadData(long start, long count)
    {
       
        HttpGet getRequest;
        try
        {
            getRequest = new HttpGet(baseURL + permissionListURL + "?start=" + start + "&count=" + count + "&sortField=" + URLEncoder.encode(sortField,"UTF-8") + "&sortOrder=" + URLEncoder.encode("" + sortOrder, "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(PermissionsController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        HttpClient client = restController.getClient();
        HttpResponse response = null;
        try
        {
            response = restController.ProcessRequest(getRequest);
        }
        catch(IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data loading error");
            alert.setHeaderText("Load permissions error");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return;
        }
        evaluatePermissions(response);
    }
    
    private Collection<Permission> data;
    private long count = 0;

    private void evaluatePermissions(HttpResponse response)
    {
        try
        {
            ObjectMapper objMapper = new ObjectMapper();
            //read result as a JsonListResult<SIEVASSession>
            //NOTE: users and groups are missing in the local object since it is not needed
           
            JsonListResult<Permission> result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<Permission> >(){});
            
            if (tableView!=null)
            {
                
                tableView.getItems().removeAll(tableView.getItems());
                tableView.getItems().addAll(result.getData());
                
            }
            this.data = result.getData();
            this.count = result.getTotal();
            long pageCount = this.count/PAGE_SIZE;
            if (this.count % PAGE_SIZE != 0)
                pageCount++;
            this.pagination.setPageCount((int)pageCount);
        }
        catch (IOException ex)
        {
            Logger.getLogger(SessionListController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data loading error");
            alert.setHeaderText("Load permissions error");
            alert.setContentText(ex.getMessage());

            alert.showAndWait();
        }
    }*/

    
    
}
