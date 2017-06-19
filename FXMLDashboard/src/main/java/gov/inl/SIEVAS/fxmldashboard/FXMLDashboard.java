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

// JavaFX Imports
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
    
    private FXMLDashboardController controller;
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/FXMLDashboard.fxml"));
        Parent root = fxmlLoader.load();
        controller = (FXMLDashboardController) fxmlLoader.getController();
        
        Scene scene = new Scene(root);
        
        stage.setTitle("VR Dashboard");
        stage.setScene(scene);
        stage.show();     
    }

    @Override
    public void stop() throws Exception {    
        super.stop();
        controller.destroy();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }      
}