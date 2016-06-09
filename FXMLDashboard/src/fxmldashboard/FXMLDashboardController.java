/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxmldashboard;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 *
 * @author stermj
 */
public class FXMLDashboardController implements Initializable {
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            String[] command = {(String) ((Button)(event.getSource())).getProperties().get("script")};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
        } catch (Exception e) {
            if (e instanceof NullPointerException) {
                System.out.println("NullPointerException. This button likely needs a \"script\" property.");
            } else {
                System.out.println(e.getMessage());
            }
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
