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
package gov.inl.SIEVAS.adminconsole.permission;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.fasterxml.jackson.core.type.TypeReference;
import gov.inl.SIEVAS.adminconsole.BaseController;
import gov.inl.SIEVAS.common.JsonListResult;
import gov.inl.SIEVAS.entity.Permission;
import java.util.List;
import javafx.scene.control.MenuItem;

/**
 * Class to handle the permissions listing.
 *
 * @author monejh
 */
public class PermissionsController extends BaseController<Permission>//implements Initializable
{

    @Override
    public String getObjectEditFXML()
    {
        return "/fxml/PermissionEdit.fxml";
    }

    @Override
    public Permission createObject()
    {
        return new Permission();
    }

    @Override
    public String getObjectTextName()
    {
        return "permission";
    }

    @Override
    public String getObjectURL()
    {
        return "api/permissions/";
    }

    @Override
    public List<MenuItem> getContextMenuItems()
    {
        return null;
    }
    
    @Override
    public TypeReference<JsonListResult<Permission>> getTypeReferenceForList()
    {
        return new TypeReference<JsonListResult<Permission > >(){};
    }
    
    
    
}
