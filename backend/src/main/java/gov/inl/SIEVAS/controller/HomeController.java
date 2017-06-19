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
package gov.inl.SIEVAS.controller;

import gov.inl.SIEVAS.common.Utility;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Handles the home page
 * @author monejh
 */
@Controller
public class HomeController
{
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getHome()
    {
        return Utility.getHomeURL();
    }
    
    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    public String getMenu()
    {
        return Utility.getHomeURL();
    }
    
    
}
