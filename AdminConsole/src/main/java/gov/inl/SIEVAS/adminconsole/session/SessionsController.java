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