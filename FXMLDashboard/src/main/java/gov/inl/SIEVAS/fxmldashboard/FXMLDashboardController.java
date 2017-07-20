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
package gov.inl.SIEVAS.fxmldashboard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

/**
 *
 * @author stermj
 */
public class FXMLDashboardController implements Initializable {
    
    private JMSProducer producer;
    
    @FXML
    private Button btnQuit;
        
    /**
     * Runs the script associated with the button that was pushed.
     * @param event 
     */
    @FXML
    private void handleButtonAction(ActionEvent event) {
        
        String command = (String) ((Button)(event.getSource())).getProperties().get("script");
        if ((command != null) && (!command.isEmpty()))
        {
            try
            {
                Logger.getLogger("Running Program:" + command);
                ProcessBuilder pb = new ProcessBuilder(command);
                Process p = pb.start();
            }
            catch(Exception e)
            {
                Logger.getLogger(this.getClass()).info(e);
            }
        }
        else
        {
            String kill = (String) ((Button)(event.getSource())).getProperties().get("killprocess");
            if ((kill != null) && (!kill.isEmpty()))
            {
                Runtime rt = Runtime.getRuntime();
                try
                {
                    if (System.getProperty("os.name").toLowerCase().indexOf("windows") > -1)
                    {
                        Logger.getLogger(this.getClass()).info("Running " + "taskkill /F /IM " + kill + "");
                        String cmds[] = {"taskkill","/F", "/IM",kill};
                        Runtime.getRuntime().exec(cmds);
                    }
                     else
                    {
                        Logger.getLogger(this.getClass()).info("Running " + "killall -9 " + kill);
                        String cmds[] = {"killall","-9",kill};
                        Runtime.getRuntime().exec(cmds);
                    }
                }
                catch(Exception e)
                {
                    Logger.getLogger(this.getClass()).info(e);
                }
            }
            else
            {
                Logger.getLogger(this.getClass()).info("No process set or kill process given!");
            }
        }
        
        
    }
    
    /**
     * Terminates all programs run from the dashboard.
     * @param event 
     */
    @FXML
    private void handleTerminateAction(ActionEvent event) throws InterruptedException {
        producer.sendMessage("TERMINATE.");
    }
    
    
    /**
     * Quits the dashboard
     * @param event 
     */
    @FXML
    private void handleQuitAction(ActionEvent event)
            throws InterruptedException
    {
        Stage stage = (Stage) btnQuit.getScene().getWindow();
        // do what you have to do
        stage.close();
    }
    
    /**
     * Initialize this class.
     * @param url
     * @param rb 
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        producer = new JMSProducer();
        System.out.println("FXMLDashboardController initialization complete.");
    }    
    
    /**
     * Spawns a new thread running that runs the given object.
     * @param runnable - The object to be executed.
     * @param daemon 
     */
    public static void thread(Runnable runnable, boolean daemon) {
        Thread brokerThread = new Thread(runnable);
        brokerThread.setDaemon(daemon);
        brokerThread.start();
    }
    
    /**
     * Handles cleanup for the FXMLDashboardController class.
     */
    public void destroy() {
        producer.close();
    }
}
