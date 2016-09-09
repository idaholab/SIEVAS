/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.live.dvrcontrol;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

/**
 *
 * @author monejh
 */
public class SessionListController implements Initializable
{
    public static String sessionListURL = "api/sessions/";
    public static String CLIENT_URL = "tcp://localhost:61616";
    
    private String baseURL = "";
    
    private RestController restController;
    
    @FXML
    private TableView tableView;
    
    public void LoadData()
    {
       
        HttpGet getRequest = new HttpGet(baseURL + sessionListURL);
        HttpClient client = restController.getClientTrustingAllSSLCerts();
        HttpResponse response = null;
        try
        {
            response = client.execute(getRequest);
        }
        catch(IOException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data loading error");
            alert.setHeaderText("Load session error");
            alert.setContentText(e.getMessage());

            alert.showAndWait();
            return;
        }
        //printResponse(response);
        evaluateSessions(response);
    }
    
    private void evaluateSessions(HttpResponse response)
    {
        try
        {
            ObjectMapper objMapper = new ObjectMapper();
            //read result as a JsonListResult<LIVESession>
            //NOTE: users and groups are missing in the local object since it is not needed
            JsonListResult<LIVESession> result = objMapper.readValue(response.getEntity().getContent(), new TypeReference<JsonListResult<LIVESession> >(){});
            tableView.setItems(FXCollections.observableArrayList(result.getData()));
        }
        catch (IOException ex)
        {
            Logger.getLogger(SessionListController.class.getName()).log(Level.SEVERE, null, ex);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Data loading error");
            alert.setHeaderText("Load session error");
            alert.setContentText(ex.getMessage());

            alert.showAndWait();
        }
    }
    
    private void handleMouseClick(LIVESession session) throws IOException
    {
        if (session!=null)
        {
            System.out.println("Clicked " + session.getId() + ":" + session.getName());
            //success of session selection, show DVR Controls
            Stage stage = (Stage) tableView.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DVR.fxml"));
            Parent root = loader.load();
            DVRController controller = loader.getController();

            controller.setRestController(restController);
            controller.setBaseURL(baseURL);
            controller.setSession(session);
            controller.Load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/Styles.css");

            stage.setTitle("SIEVAS DVR Controls - Session " + session.getName());
            stage.setScene(scene);
            stage.show();
//            try
//            {
//                connectToLIVE(session);
//            }
//            catch(JMSException | InterruptedException e)
//            {
//                Logger.getLogger(SessionListController.class.getName()).log(Level.SEVERE, null, e);
//            }
        }
            
        
    }
    
   

    public void setBaseURL(String url)
    {
        this.baseURL = url;
    }
    
    
    public void setRestController(RestController rest)
    {
        this.restController = rest;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        tableView.setRowFactory(tv -> {
            TableRow row = new TableRow();
            row.setOnMouseClicked(event -> {
                if ((event.getClickCount() == 2) && (!row.isEmpty()))
                {
                    try
                    {
                        this.handleMouseClick((LIVESession)row.getItem());
                    }
                    catch(IOException e)
                    {
                        Logger.getLogger(DVRController.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
            });
            return row;
        });
        
    }
    
}
