/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.session;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.inl.SIEVAS.adminconsole.BaseController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import gov.inl.SIEVAS.adminconsole.SIEVASSession;
import java.util.List;
import javafx.scene.control.MenuItem;

/**
 * Class for session grid view
 *
 * @author monejh
 */
public class SessionsController extends BaseController<SIEVASSession>
{

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/SessionEdit.fxml";
    }

    @Override
    public SIEVASSession createObject()
    {
        return new SIEVASSession();
    }

    @Override
    public String getObjectTextName()
    {
        return "session";
    }

    @Override
    public String getObjectURL()
    {
        return "api/sessions/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<SIEVASSession>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<SIEVASSession > >(){};
    }
    
    
    
}