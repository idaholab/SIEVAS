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