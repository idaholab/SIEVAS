/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.user;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.inl.SIEVAS.adminconsole.BaseController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.permissiongroup.PermissionGroup;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class UserController extends BaseController<UserInfo>//implements Initializable
{
    @FXML
    private TableColumn colEnabled;
    
    @FXML
    private TableColumn colLocked;
    
    @FXML
    private TableColumn colExpired;
    
    @Override
    public void initialize()
    {
        super.initialize();
        Callback<TableColumn<UserInfo, Boolean>, TableCell<UserInfo, Boolean>> cellFactory = new Callback<TableColumn<UserInfo, Boolean>, TableCell<UserInfo, Boolean>>() {

            public TableCell<UserInfo, Boolean> call(TableColumn<UserInfo, Boolean> p) {

                return new CheckBoxTableCell<UserInfo, Boolean>(new Callback<Integer, ObservableValue<Boolean>>(){
                    @Override
                    public ObservableValue<Boolean> call(Integer param)
                    {
                        
                    return new SimpleBooleanProperty(p.getCellData(param).booleanValue());
                    }
                    
                });
                }
            };
        colEnabled.setCellFactory(cellFactory);
        colExpired.setCellFactory(cellFactory);
        colLocked.setCellFactory(cellFactory);

            

        
    }
    

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/UserEdit.fxml";
    }

    @Override
    public UserInfo createObject()
    {
        UserInfo user = new UserInfo();
        user.setFirstName("");
        user.setLastName("");
        user.setEnabled(true);
        user.setPassword("");
        user.setPassword2("");
        user.setUsername("");
        user.setPermissionGroupCollection(new ArrayList<PermissionGroup>());
        
        return user;
    }

    @Override
    public String getObjectTextName()
    {
        return "user";
    }

    @Override
    public String getObjectURL()
    {
        return "api/users/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<UserInfo>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<UserInfo > >(){};
    }
    
    
    
    
    
    
}