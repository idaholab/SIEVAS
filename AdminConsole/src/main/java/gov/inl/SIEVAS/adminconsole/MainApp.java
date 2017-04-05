package gov.inl.SIEVAS.adminconsole;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/***
 * Handles the main application startup
 * @author monejh
 */
public class MainApp extends Application {
    
    
    public static final String LOGIN_FXML = "/fxml/Login.fxml";
    public static final String CSS_URL = "/styles/Styles.css";
    public static final String LOGIN_TITLE = "SIEVAS Login";

    /***
     * Starts the main page for application. This presents a login window.
     * @param stage
     * @throws Exception 
     */
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(LOGIN_FXML));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS_URL);
        
        stage.setTitle(LOGIN_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
