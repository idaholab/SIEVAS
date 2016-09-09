/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.controller;

/**
 *
 * @author monejh
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.NbodyDAO;
import gov.inl.LIVE.DAO.NbodyInfoDAO;
import gov.inl.LIVE.common.JsonError;
import gov.inl.LIVE.common.Utility;
import gov.inl.LIVE.entity.Nbody;
import gov.inl.LIVE.entity.NbodyInfo;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * REST Controller for permissions
 * @author monejh
 */
@Controller
public class NbodyController
{
    @Autowired
    ObjectMapper objMapper;
    
    @Autowired
    NbodyInfoDAO nbodyInfoDAO;
    
    @Autowired
    NbodyDAO nbodyDAO;
    
    
    /***
     * Gets the home URL.
     * @return The URL of the home page.
     */
    private String getHome(){ return Utility.getHomeURL(); }
    
    //Next four handle the various routes for angular 2.
    @RequestMapping(value = "/nbody", method = RequestMethod.GET)
    public String getNbodies() { return getHome(); }
    
    @RequestMapping(value = "/nbody/", method = RequestMethod.GET)
    public String getNbodiesWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/nbody/edit/{id}", method = RequestMethod.GET)
    public String getNbodyById(){ return getHome(); }
    
    @RequestMapping(value = "/nbody/create", method = RequestMethod.GET)
    public String getNbodyCreate() { return getHome(); }
    
    
    
    
    /***
     * Gets a permission by id.
     * @param id The id of the permission to get.
     * @return The permission has JSON or the error result.
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/api/nbody/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getNbodyById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<NbodyInfo,NbodyInfo> triple = nbodyInfoDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<NbodyInfo> root = triple.getRoot();
        
        
        NbodyInfo info = nbodyInfoDAO.findById(id);
        if (info == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(objMapper.writeValueAsString(info), HttpStatus.OK);
        
    }
    
    @RequestMapping(value = "/api/nbody/getData", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getNbodyData()
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<Nbody,Nbody> triple = nbodyDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<Nbody> root = triple.getRoot();
        
        Predicate pred = cb.and(cb.ge(root.get("step"), 0),cb.le(root.get("step"), 100000));
        Order[] orderBy = { cb.asc(root.get("step")), cb.asc(root.get("planetNumber"))};
        List<Nbody> list = nbodyDAO.findByCriteria(triple, orderBy, 0, -1, pred);
        
        
        if (list == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(objMapper.writeValueAsString(list), HttpStatus.OK);
        
    }
    
   
    
    /***
     * Handles any exception in processing above.
     * @param req The request object.
     * @param exception The exception that occurred.
     * @return The JsonError message for the user.
     * @throws JsonProcessingException 
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(HttpServletRequest req, Exception exception) throws JsonProcessingException
    {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        exception.printStackTrace(pw);
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(exception.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



