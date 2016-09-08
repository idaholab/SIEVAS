package gov.inl.live.dvrcontrol;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;


public class LoginController implements Initializable
{
    
    @FXML
    private TextField username;
    
    @FXML
    private PasswordField password;
    
    @FXML
    private TextField url;
    
    private RestController restController = new RestController();
    
    public static String LOGIN_URL = "login";
    public static String SESSION_LIST_URL = "api/sessions/";
    public static String CLIENT_URL = "tcp://localhost:61616";
    
    
    @FXML
    private void handleLogin(ActionEvent event) throws UnsupportedEncodingException, NoSuchAlgorithmException, KeyManagementException, IOException
    {
        
        //setup to allow self signed certs
        HttpClient client = restController.getClientTrustingAllSSLCerts();
        //handle login
        String loginURL = url.getText();
        loginURL = loginURL.trim();
        if (!loginURL.endsWith("/"))
            loginURL += "/";
        loginURL += LOGIN_URL; 
        HttpPost request = new HttpPost(loginURL);
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("username", username.getText()));
            nvps.add(new BasicNameValuePair("password", password.getText()));
            request.setEntity(new UrlEncodedFormEntity(nvps));
            HttpResponse response = client.execute(request);
            //printResponse(response);
            response.getEntity().consumeContent();

            boolean error = false;
            Header[] headers = response.getHeaders("Location");
            if (headers.length>0)
            {
                for(Header header: headers)
                {
                    if (header.getValue().endsWith("?error"))
                        error = true;
                }
            }

            if (error)
            {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,"Login Failure");
                //display error to user
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Login processing error");
                alert.setContentText("Username/Password not accepted or Server URL is incorrect!");

                alert.showAndWait();
            }
            else
            {
                //success of login, show session list
                Stage stage = (Stage) username.getScene().getWindow();
                
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SessionList.fxml"));
                //Parent root = FXMLLoader.load(getClass().getResource("/fxml/SessionList.fxml"));
                Parent root = loader.load();
                SessionListController controller = loader.getController();
        
                controller.setRestController(restController);
                String baseURL = url.getText();
                baseURL = baseURL.trim();
                if (!baseURL.endsWith("/"))
                    baseURL += "/";
                controller.setBaseURL(baseURL);
                controller.LoadData();
                Scene scene = new Scene(root);
                scene.getStylesheets().add("/styles/Styles.css");

                stage.setTitle("SIEVAS - Select a Session");
                stage.setScene(scene);
                stage.show();
                
                
                //stage.close();
            }
        
    }
    
    @FXML
    private void handleCancel(ActionEvent event)
    {
        System.exit(0);
        
    }
    
    @FXML
    public void onKeyPressed(KeyEvent event)
    {
       if (event.getCode().equals(KeyCode.ENTER))
       {
           try
           {
               handleLogin(new ActionEvent());
           }
           catch (NoSuchAlgorithmException | KeyManagementException | IOException ex)
           {
               Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
    }    
}
