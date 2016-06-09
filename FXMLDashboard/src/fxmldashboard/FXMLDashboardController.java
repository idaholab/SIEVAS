/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxmldashboard;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 *
 * @author stermj
 */
public class FXMLDashboardController implements Initializable {
    
    @FXML
    private Button btnDevilsWash;
    private Button btnEV;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            String[] command = {"/home/stermj/devilswashScript.sh"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleEVButtonAction(ActionEvent event) {
        try {
            String[] command = {"/home/stermj/evScript.sh"};
            ProcessBuilder pb = new ProcessBuilder(command);
            Process p = pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
