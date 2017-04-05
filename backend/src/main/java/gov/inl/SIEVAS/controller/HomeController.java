/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
