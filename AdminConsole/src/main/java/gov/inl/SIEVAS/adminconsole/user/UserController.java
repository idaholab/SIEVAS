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
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class UserController extends BaseController<UserInfo>//implements Initializable
{
    
    

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/UserEdit.fxml";
    }

    @Override
    public UserInfo createObject()
    {
        return new UserInfo();
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