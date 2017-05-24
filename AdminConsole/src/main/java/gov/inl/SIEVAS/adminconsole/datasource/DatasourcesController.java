/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.adminconsole.datasource;

import com.fasterxml.jackson.core.type.TypeReference;
import gov.inl.SIEVAS.adminconsole.BaseController;
import gov.inl.SIEVAS.adminconsole.JsonListResult;
import java.util.List;
import javafx.scene.control.MenuItem;


/***
 * Class to handle grid for datasources
 * @author monejh
 */
public class DatasourcesController extends BaseController<Datasource>//implements Initializable
{

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/DatasourceEdit.fxml";
    }

    @Override
    public Datasource createObject()
    {
        return new Datasource();
    }

    @Override
    public String getObjectTextName()
    {
        return "datasource";
    }

    @Override
    public String getObjectURL()
    {
        return "api/datasources/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<Datasource>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<Datasource > >(){};
    }
    
    
    
}
