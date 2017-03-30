package gov.inl.SIEVAS.adminconsole.permissiongroup;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.fasterxml.jackson.core.type.TypeReference;
import gov.inl.SIEVAS.adminconsole.BaseController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import java.util.List;
import javafx.scene.control.MenuItem;

/**
 * FXML Controller class
 *
 * @author monejh
 */
public class PermissionGroupController extends BaseController<PermissionGroup>//implements Initializable
{

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/PermissionGroupEdit.fxml";
    }

    @Override
    public PermissionGroup createObject()
    {
        return new PermissionGroup();
    }

    @Override
    public String getObjectTextName()
    {
        return "group";
    }

    @Override
    public String getObjectURL()
    {
        return "api/permissiongroups/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<PermissionGroup>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<PermissionGroup > >(){};
    }
    
    
    
}