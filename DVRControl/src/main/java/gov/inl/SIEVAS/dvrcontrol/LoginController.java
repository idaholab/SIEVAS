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
package gov.inl.SIEVAS.dvrcontrol;

import java.io.IOException;
import java.net.URL;
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
    
    private RestController restController = new RestController();
    
    public static String LOGIN_URL = "login";
    public static String SESSION_LIST_URL = "api/sessions/";
    public static String CLIENT_URL = "tcp://localhost:61616";
    
    
    /***
     * Handles the login request or the user using the specified user, 
     * password, and base URL.
     * @param event 
     */
    @FXML
    private void handleLogin(ActionEvent event)
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
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,"Login Failure", e);
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Login Error");
                alert.setHeaderText("Login processing error");
                alert.setContentText(e.getMessage());

                alert.showAndWait();
                return;
            }

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
                Parent root;
                try
                {
                    root = loader.load();
                }
                catch(IOException e)
                {
                    Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE,"Failure loading dialog", e);
                    return;
                }
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
