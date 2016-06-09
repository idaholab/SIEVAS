/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxmldashboard;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author stermj
 */
public class FXMLDashboard extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
//        ScriptButton testButton = new ScriptButton();
//        testButton.setScript("/home/stermj/evScript.");
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDashboard.fxml"));
        
        Scene scene = new Scene(root, 1300, 1000);
        
        stage.setTitle("VRMenu");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
