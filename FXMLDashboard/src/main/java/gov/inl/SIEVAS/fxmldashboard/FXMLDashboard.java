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