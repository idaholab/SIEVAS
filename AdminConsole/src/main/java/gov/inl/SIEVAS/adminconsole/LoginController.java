package gov.inl.SIEVAS.adminconsole;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/***
 * Handles login controller functions
 * @author monejh
 */
public class LoginController implements Initializable
{
    
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password;
    
    @FXML
    private TextField url;
    
    private final RestController restController = RestController.getInstance();
    
    private boolean running = false;
    private EventHandler<ActionEvent> handler = null;
    
    private static final String LOGIN_URL = "login";
    private static final String PERMISSION_CHECK_URL = "api/permissions/user/";
    private static final String ADMIN_PERMISSION = "admin";
    private static final String PATH_SEPARATOR = "/";
    private static final String USERNAME_PARAM = "username";
    private static final String PASSWORD_PARAM = "password";
    private static final String LOCATION_HEADER = "Location";
    private static final String ERROR_PATTERN = "?error";
    private static final String ADMIN_CONSOLE_MAIN_FXML = "/fxml/AdminConsole.fxml";
    private static final String LOGIN_ERROR_TITLE = "Login Error";
    private static final String LOGIN_PROCESSING_ERROR = "Login processing error";
    private static final String LOGIN_FAILURE_MESSAGE = "Login Failure";
    private static final String INVALID_PASSWORD = "Username/Password not accepted or Server URL is incorrect!";
    private static final String FAILURE_LOAD_MAIN_WINDOW = "Failure loading main window";
    private static final String CSS_URL = "/styles/Styles.css";
    private static final String MAIN_WINDOW_TITLE = "SIEVAS Administration Console";
    private static final String ERROR_TITLE = "Error";
    private static final String PERMISSION_ERROR_TEXT = "Permission Error";
    private static final String ADMIN_PERM_ERROR = "User does not have administrative privileges.";
    private static final String DATA_LOAD_ERROR = "Data loading error";
    private static final String LOAD_PERM_ERROR = "Load permission error";
    
    
    public void setRunning(boolean running)
    {
        this.running = running;
    }
    
    public void setEventHandler(EventHandler<ActionEvent> handler)
    {
        this.handler = handler;
    }
    
    /***
     * Handles the login request or the user using the specified user, 
     * password, and base URL.
     * @param event 
     */
    @FXML
    private void handleLogin(ActionEvent event)
    {
        
        //setup to allow self signed certs
        HttpClient client = restController.getClient();
        //handle login
        String baseURL = url.getText();
        baseURL = baseURL.trim();
        if (!baseURL.endsWith(PATH_SEPARATOR))
            baseURL += PATH_SEPARATOR;
        
        String loginURL = baseURL;
        loginURL += LOGIN_URL; 
        HttpPost request = new HttpPost(loginURL);
        List <NameValuePair> nvps = new ArrayList <>();
        nvps.add(new BasicNameValuePair(USERNAME_PARAM, username.getText()));
        nvps.add(new BasicNameValuePair(PASSWORD_PARAM, password.getText()));
        HttpResponse response;
        try
        {
            request.setEntity(new UrlEncodedFormEntity(nvps));
            response = client.execute(request);
            //printResponse(response);
            response.getEntity().consumeContent();
        }
        catch(IOException e)
        {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,LOGIN_FAILURE_MESSAGE, e);
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(LOGIN_ERROR_TITLE);
            alert.setHeaderText(LOGIN_PROCESSING_ERROR);
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return;
        }

        boolean error = false;
        
        Header[] headers = response.getHeaders(LOCATION_HEADER);
        if (headers.length>0)
        {
            for(Header header: headers)
            {
                if (header.getValue().endsWith(ERROR_PATTERN))
                    error = true;
            }
        }

        if (error)
        {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,LOGIN_FAILURE_MESSAGE);
            //display error to user
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle(LOGIN_ERROR_TITLE);
            alert.setHeaderText(LOGIN_PROCESSING_ERROR);
            alert.setContentText(INVALID_PASSWORD);
            alert.showAndWait();
        }
        else
            handlePermissionCheck(baseURL);
        
    }
    
    /***
     * Handles the response of the session list
     * @param response  The HTTP response
     */
    private void evaluatePermissionCheck(HttpResponse response)
    {
        if (response.getStatusLine().getStatusCode() == 200)
        {
        
            if (running)
            {
                if (handler!=null)
                {
                    ActionEvent event = new ActionEvent();
                    handler.handle(event);
                }
                Stage stage = (Stage) username.getScene().getWindow();
                stage.close();
                return;
            }
            else
            {
            //success of login, show session list
                Stage stage = (Stage) username.getScene().getWindow();

                FXMLLoader loader = new FXMLLoader(getClass().getResource(ADMIN_CONSOLE_MAIN_FXML));
                Parent root;
                try
                {
                    root = loader.load();
                }
                catch(IOException e)
                {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,FAILURE_LOAD_MAIN_WINDOW, e);
                    return;
                }
                AdminConsoleController controller = loader.getController();

                String baseURL = url.getText();
                baseURL = baseURL.trim();
                if (!baseURL.endsWith(PATH_SEPARATOR))
                    baseURL += PATH_SEPARATOR;
                controller.setBaseURL(baseURL);
                controller.LoadData();
                
                Scene scene = new Scene(root);
                scene.getStylesheets().add(CSS_URL);

                stage.setTitle(MAIN_WINDOW_TITLE);
                stage.setScene(scene);
                stage.show();
            }


        }
        else
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(ERROR_TITLE);
            alert.setHeaderText(PERMISSION_ERROR_TEXT);
            alert.setContentText(ADMIN_PERM_ERROR);
            alert.showAndWait();
        }
    }
    
    
    private void handlePermissionCheck(String baseURL)
    {
        HttpGet getRequest = new HttpGet(baseURL + PERMISSION_CHECK_URL + ADMIN_PERMISSION);
        HttpClient client = restController.getClient();
        HttpResponse response;
        try
        {
            response = client.execute(getRequest);
            EntityUtils.consume(response.getEntity());
        }
        catch(IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(DATA_LOAD_ERROR);
            alert.setHeaderText(LOAD_PERM_ERROR);
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return;
        }
        evaluatePermissionCheck(response);
    }
    
    /***
     * Handles the cancel button by quitting
     * @param event 
     */
    @FXML
    private void handleCancel(ActionEvent event)
    {
        System.exit(0);
        
    }
    
    /***
     * Intercepts the enter key to process login on the password field.
     * @param event 
     */
    @FXML
    public void onKeyPressed(KeyEvent event)
    {
       if (event.getCode().equals(KeyCode.ENTER))
       {
           handleLogin(new ActionEvent());
       }
    }
    
    /***
     * Nothing to do here for this controller
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        
    }    
}
