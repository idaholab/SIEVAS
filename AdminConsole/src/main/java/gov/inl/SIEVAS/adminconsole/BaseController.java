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
package gov.inl.SIEVAS.adminconsole;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

/**
 * Abstract class for the basecontroller of all the main views. Shows a grid, 
 *  pagination, and new button.
 * @author monejh
 */
public abstract class BaseController<T extends IIdentifier>
{
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private TableView tableView;
    
    @FXML
    private Button btnNew;
    
    /*Need to override these */
    /***
     * 
     * @return Gets the FXML path of the edit window
     */
    public abstract String getObjectEditFXML();
    
    /***
     * Create a new object of type T
     * @return the newly created object.
     */
    public abstract T createObject();
    
    /***
     * Gets the text name to display to user
     * @return The string of the text object name
     */
    public abstract String getObjectTextName();
    
    /***
     * Gets the URL for listing/deleting the object
     * @return The relative URL to the base URL to use.
     */
    public abstract String getObjectURL();
    
    /***
     * Gets the type reference for the listing JSON.
     * @return The strong typereference for the T object.
     */
    public abstract TypeReference<JsonListResult<T > > getTypeReferenceForList();
    
    /***
     * The extra content menu items you want to display for each row.
     * @return 
     */
    public abstract List<MenuItem> getContextMenuItems();
    
    protected static final String DEFAULT_SORT_FIELD = "id";
    protected static final String CSS_URL = "/styles/Styles.css";
    protected static final String DELETE_MENU_ITEM = "Delete";
    protected static final String DATA_IO_ERROR_MSG = "Data I/O error";
    protected final String DELETE_ERROR_MSG = "Delete " + getObjectTextName() + " error";
    protected static final String DATA_DELETING_ERROR_MSG = "Data deleting error";
    protected static final String CONTENT_FAILURE_MSG = "Failure loading content";
    protected static final String EDIT_TEXT = "Edit";
    protected static final String SPACE = " ";
    protected static final String CREATE_TEXT = "Create";
    protected static final double SPACING = 5.0;
    protected static final String LOAD_ERROR_MSG = "Data loading error";
    protected final String DATA_LOADING_ERROR_MSG = "Load " + getObjectTextName() + " error";
    protected static final String URL_PARAM_START = "?";
    protected static final String START_PARAM_NAME = "start";
    protected static final String COUNT_PARAM_NAME = "count";
    protected static final String SORT_FIELD_PARAM_NAME = "sortField";
    protected static final String SORT_ORDER_PARAM_NAME = "sortOrder";
    protected static final String PARAM_SEPARATOR = "&";
    protected static final String EQUALS = "=";
    protected static final String DEFAULT_ENCODING = "UTF-8";
    
    
    private String baseURL = "";
    private final RestController restController = RestController.getInstance();
    private String sortField = DEFAULT_SORT_FIELD;
    private int sortOrder = 1;
    private long lastStartIndex = 0;
    private final int PAGE_SIZE = 10;
    private long count = 0;
    
    
    
    /***
     * Sets the base URL for the server
     * @param url 
     */
    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    /***
     * Handles the window init. Sets the callback for pagination, context menu, 
     *  and double clicks to edit.
     */
    @FXML
    public void initialize()//URL url, ResourceBundle rb)
    {
        pagination.setPageFactory(new Callback<Integer, Node>()
        {
            @Override
            public Node call(Integer pageIndex)
            {
                return createPage(pageIndex);
            }
        });
        
        tableView.setRowFactory((Object tv) -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if ((event.getClickCount() == 2) && (!row.isEmpty()))
                {
                    this.handleMouseClick((T)row.getItem());
                }
            });
            MenuItem deleteItem = new MenuItem(DELETE_MENU_ITEM);
            deleteItem.setOnAction(new EventHandler<ActionEvent>()
            {
                @Override
                public void handle(ActionEvent event)
                {
                    T obj = (T)row.getItem();
                    handleDelete(obj);
                }
            });
            
            List<MenuItem> items = getContextMenuItems();
            if (items == null)
                items = new ArrayList<>();
            items.add(deleteItem);
            ContextMenu menu = new ContextMenu(items.toArray(new MenuItem[0]));
            row.setContextMenu(menu);
            return row;
        });
        
    }
    
    /***
     * Handles the context menu delete option. Makes the rest call to delete.
     * @param obj The object to delete
     */
    private void handleDelete(T obj)
    {
        String url = baseURL + getObjectURL() + obj.getId();
        
        HttpDelete request = new HttpDelete(url);
        HttpResponse response;
        try
        {
            response = restController.ProcessRequest(request);
        }
        catch(IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_IO_ERROR_MSG);
            alert.setHeaderText(DELETE_ERROR_MSG);
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return;
        }
        if (response.getStatusLine().getStatusCode()!=200)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_DELETING_ERROR_MSG);
            alert.setHeaderText(DELETE_ERROR_MSG);
            alert.setContentText(response.getStatusLine().getReasonPhrase());

            alert.showAndWait();
        }
        try
        {
            response.getEntity().consumeContent();
        } 
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        LoadData(lastStartIndex,PAGE_SIZE);
    }
    
    /***
     * Handles a change in the sort for the table.
     */
    private void handleSortChange()
    {
        ObservableList<TableColumn<T,?>> list = tableView.getSortOrder();
        sortField = DEFAULT_SORT_FIELD;
        for(TableColumn<T,?> col: list)
        {
            sortField = col.getId();
            if (col.getSortType() == TableColumn.SortType.ASCENDING)
                sortOrder = 1;
            else
                sortOrder = -1;
            
        }
        LoadData(lastStartIndex,PAGE_SIZE);
    }
    
    
    
    /***
     * Handles the double click of the mouse on a row. Opens the edit window.
     * @param obj The object clicked.
     */
    private void handleMouseClick(T obj)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getObjectEditFXML()));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG, e);
            return;
        }
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS_URL);
        
        Stage stage = new Stage();
        stage.setTitle(getObjectTextName() + SPACE + EDIT_TEXT);
        stage.setScene(scene);
        
        
        BaseEditController<T> controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.setObject(obj);
        controller.setCreateMode(false);
        controller.LoadData();
        stage.showAndWait();
        LoadData(pagination.getCurrentPageIndex()*PAGE_SIZE,PAGE_SIZE);
        
        
    }
    
    /***
     * Handles a click on the new button. Opens the edit window with a 
     *  new object.
     */
    @FXML
    private void onNewClick()
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(getObjectEditFXML()));
        Parent root;
        try
        {
            root = loader.load();
        }
        catch(IOException e)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,CONTENT_FAILURE_MSG, e);
            return;
        }
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS_URL);
        
        Stage stage = new Stage();
        stage.setTitle(getObjectTextName() + SPACE + CREATE_TEXT);
        stage.setScene(scene);
        
        T obj = createObject();
        BaseEditController<T> controller = loader.getController();
        controller.setBaseURL(baseURL);
        controller.setObject(obj);
        controller.setCreateMode(true);
        controller.LoadData();
        stage.showAndWait();
        LoadData(pagination.getCurrentPageIndex()*PAGE_SIZE,PAGE_SIZE);
        
        
    }
    
    
    /***
     * Callback for pagination to create new page for the table.
     * @param pageIndex The index of the page to render.
     * @return The Pane contents of the page. Returns the new button and grid.
     */
    public Pane createPage(int pageIndex)
    {
        
        long start = pageIndex*PAGE_SIZE;
        lastStartIndex = start;
        LoadData(start, PAGE_SIZE);
        VBox box = new VBox(SPACING, btnNew, tableView);
        box.setPadding(new Insets(5.0));
        return box;
    }
    
    /***
     * Handles the initial loading of data. Call after all values are set.
     */
    public void LoadData()
    {
        tableView.setSortPolicy(new Callback<TableView, Boolean>(){
            @Override
            public Boolean call(TableView param)
            {
                handleSortChange();
                return true;
            }
            
        });
        LoadData(0,PAGE_SIZE);
    }
    
    /***
     * Handles loading a specific page for the table.
     * @param start The start record to show.
     * @param count The number of records to show.
     */
    public void LoadData(long start, long count)
    {  
        HttpGet getRequest;
        try
        {
            getRequest = new HttpGet(baseURL + getObjectURL() + URL_PARAM_START + START_PARAM_NAME + EQUALS + start
                            + PARAM_SEPARATOR + SORT_FIELD_PARAM_NAME + EQUALS + URLEncoder.encode(sortField,DEFAULT_ENCODING)
                            + PARAM_SEPARATOR + SORT_ORDER_PARAM_NAME + EQUALS + URLEncoder.encode("" + sortOrder,DEFAULT_ENCODING));
                    
                    //"?start=" + start + "&count=" + count + "&sortField=" + URLEncoder.encode(sortField,"UTF-8") + "&sortOrder=" + URLEncoder.encode("" + sortOrder, "UTF-8"));
        }
        catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            return;
        }
        HttpResponse response = null;
        try
        {
            response = restController.ProcessRequest(getRequest);
        }
        catch(Exception e)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            
            /*Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_LOADING_ERROR_MSG);
            alert.setHeaderText(LOAD_ERROR_MSG);
            alert.setContentText(e.getMessage());

            alert.showAndWait();*/
            return;
        }
        evaluateResults(response);
    }
    
    
    /***
     * Handles the response of the permission list
     * @param response  The HTTP response
     */
    private void evaluateResults(HttpResponse response)
    {
        try
        {
            ObjectMapper objMapper = new ObjectMapper();
            //read result as a JsonListResult<SIEVASSession>
            //NOTE: users and groups are missing in the local object since it is not needed
            /*StringWriter writer = new StringWriter();
            String txt = IOUtils.toString(response.getEntity().getContent());*/
            JsonListResult<T> result = objMapper.readValue(response.getEntity().getContent(), getTypeReferenceForList());
            EntityUtils.consume(response.getEntity());
            
            if (tableView!=null)
            {
                
                tableView.getItems().removeAll(tableView.getItems());
                tableView.getItems().addAll(result.getData());
                
            }
            this.count = result.getTotal();
            long pageCount = this.count/PAGE_SIZE;
            if (this.count % PAGE_SIZE != 0)
                pageCount++;
            this.pagination.setPageCount((int)pageCount);
        }
        catch (IOException ex)
        {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            /*Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_LOADING_ERROR_MSG);
            alert.setHeaderText(LOAD_ERROR_MSG);
            alert.setContentText(ex.getMessage());

            alert.showAndWait();*/
        }
    }
}
