/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.PermissionDAO;
import gov.inl.LIVE.common.JsonError;
import gov.inl.LIVE.common.JsonListResult;
import gov.inl.LIVE.common.Utility;
import gov.inl.LIVE.entity.Permission;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 *
 * @author monejh
 */
@Controller
public class PermissionController
{
    @Autowired
    ObjectMapper objMapper;
    
    @Autowired
    PermissionDAO permissionDAO;
    
    
    private String getHome(){ return "home"; }
    
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    public String getPermissions() { return getHome(); }
    
    @RequestMapping(value = "/permissions/", method = RequestMethod.GET)
    public String getPermissionsWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/permissions/edit/{id}", method = RequestMethod.GET)
    public String getPermissionById(){ return getHome(); }
    
    @RequestMapping(value = "/permissions/create", method = RequestMethod.GET)
    public String getPermissionCreate() { return getHome(); }
    
    
    
    @RequestMapping(value = "/api/permissions/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getPermissions(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "sortField", defaultValue = "") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(name = "multiSortMeta", defaultValue = "") String multiSortMeta,
            @RequestParam(name = "filters", defaultValue = "") String filters
            )
            throws JsonProcessingException
    {
        System.out.println("Start:" + start);
        System.out.println("Count:" + count);
        System.out.println("sortField:" + sortField);
        System.out.println("sortOrder:" + sortOrder);
        System.out.println("multiSortMeta:" + multiSortMeta);
        System.out.println("filters:" + filters);
        
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Permission> triple = permissionDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<Permission> root = triple.getRoot();
        List<Permission> list = null;
        long total = 0;
        
        List<Predicate> predicateList = Utility.ProcessFilters(filters, cb, root, Permission.class, objMapper);
        
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Long> tripleCount = permissionDAO.getCriteriaTripleForCount();
        CriteriaBuilder cbCount = tripleCount.getCriteriaBuilder();
        Root<Permission> rootCount = tripleCount.getRoot();
        List<Predicate> predicateListCount = Utility.ProcessFilters(filters, cbCount, rootCount, Permission.class, objMapper);
        
        List<Order> orderList = Utility.ProcessOrders(sortField, sortOrder, cb, root, "permissionName", objMapper);
        if (!predicateList.isEmpty())
        {
            list = permissionDAO.findByCriteria(triple, orderList.toArray(new Order [0]), start, count,predicateList.toArray(new Predicate[0]));
            total = permissionDAO.findByCriteriaCount(tripleCount,predicateListCount.toArray(new Predicate[0]));
            
        }
        else
        {
            list = permissionDAO.getAll(triple, orderList.toArray(new Order [0]), start, count);
            total = permissionDAO.getAllCount();
        }
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, list)), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/api/permissions/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getPermissionById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Permission> triple = permissionDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<Permission> root = triple.getRoot();
        
        
        Permission perm = permissionDAO.findById(id);
        if (perm == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(objMapper.writeValueAsString(perm), HttpStatus.OK);
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/permissions/{id}", method = RequestMethod.PUT, produces = "application/json"
            )
    public ResponseEntity<String> savePermission(@PathVariable(value = "id") long id,
                @RequestBody Permission perm)
            throws JsonProcessingException, IOException
    {
        if (perm == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Long> triple = permissionDAO.getCriteriaTripleForCount();
        Root<Permission> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
            Predicate pred = cb.and(cb.equal(root.get("permissionName"), perm.getPermissionName()),
                                        cb.notEqual(root.get("id"), id));
        if (permissionDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate permission name")), HttpStatus.CONFLICT);
        
        
        permissionDAO.saveOrUpdate(perm);
        return new ResponseEntity<>(objMapper.writeValueAsString(perm), HttpStatus.OK);
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/permissions/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createPermission(@RequestBody Permission perm)
            throws JsonProcessingException
    {   
        if (perm == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Long> triple = permissionDAO.getCriteriaTripleForCount();
        Root<Permission> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Predicate pred = cb.equal(root.get("permissionName"), perm.getPermissionName());
        if (permissionDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate permission name")), HttpStatus.CONFLICT);
        
        permissionDAO.add(perm);
        return new ResponseEntity<>(objMapper.writeValueAsString(perm), HttpStatus.OK);
        
    }
    
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/permissions/{id}", method = RequestMethod.DELETE, produces = "application/json"
            )
    public ResponseEntity<String> deletePermission(@PathVariable(value = "id") long id)
            throws JsonProcessingException, IOException
    {
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Long> triple = permissionDAO.getCriteriaTripleForCount();
        
        Permission perm = permissionDAO.findById(id);
        if (perm == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        permissionDAO.remove(perm);
        return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.OK);
        
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(HttpServletRequest req, Exception exception) throws JsonProcessingException
    {
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        exception.printStackTrace(pw);
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(exception.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



