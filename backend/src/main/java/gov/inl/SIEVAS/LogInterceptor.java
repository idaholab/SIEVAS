/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 *
 * @author monejh
 */
@Component
public class LogInterceptor extends HandlerInterceptorAdapter implements WebRequestInterceptor
{

    /**
     *
     * @param request
     * @param response
     * @param handler
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, "REQUEST:" + request.getRequestURL());
        //Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, "METHOD:" + request.getMethod());
        System.out.println("METHOD:" + request.getMethod());
        Enumeration<String> headers = request.getHeaderNames();
        if (headers!=null)
            while(headers.hasMoreElements())
            {
                String header = headers.nextElement();
                Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, header + ":" + request.getHeader(header));
            }
        Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, "QUERYSTRING:" + request.getQueryString());
        Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, "CONTENTTYPE:" + request.getContentType());
        Cookie[] cookies = request.getCookies();
        if (cookies!=null)
        {
            for(Cookie cookie: cookies)
            {
                System.out.println(cookie.getName() +":" + cookie.getValue());
            }
        }
        BufferedReader reader = null;
        try
        {
            reader = request.getReader();
        }
        catch(IOException e)
        {
            return true;
        }
        if (reader!=null)
        {
            System.out.println("DATA");
            reader.lines().forEachOrdered(line -> { System.out.println(line);});
        }
        
        return true;
    }
    
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request,
            HttpServletResponse response, Object handler, Exception ex)
    {
        Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, "RESPONSE:" + response.getStatus());
        Collection<String> headers = response.getHeaderNames();
        if (headers!=null)
            for(String header: headers)
            {
                Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, header + ":" + response.getHeader(header));
            }
        Logger.getLogger(LogInterceptor.class.getName()).log(Level.SEVERE, "CONTENTTYPE:" + response.getContentType());
    }

    @Override
    public void preHandle(WebRequest wr) throws Exception
    {
        
        System.out.println("PREDESCRIPTION:" + wr.getDescription(true));
    }

    @Override
    public void postHandle(WebRequest wr, ModelMap mm) throws Exception
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void afterCompletion(WebRequest wr, Exception excptn) throws Exception
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        System.out.println("DESCRIPTION:" + wr.getDescription(true));
    }
    
}
