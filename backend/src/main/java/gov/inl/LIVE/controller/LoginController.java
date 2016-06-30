/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author monejh
 */

@Controller
public class LoginController
{
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLogin()
    {
        return "login";
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String getLogout()
    {
        return "redirect:login?logout=true";
    }
}
