/* Script Button | Marko Sterbentz 6/8/2016
 * Extends the FX button class by adding a Script string field.
 */
package fxmldashboard;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Button;
/**
 *
 * @author stermj
 */
public class ScriptButton extends Button{
    @FXML private String script;
    
    public ScriptButton() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLDashboard.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
    
    public String getScript() {
        return this.script;
    }
    public void setScript(String script) {
        this.script = script;
    }
}
