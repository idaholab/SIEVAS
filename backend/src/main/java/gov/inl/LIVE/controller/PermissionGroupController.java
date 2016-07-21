/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.PermissionGroupDAO;
import gov.inl.LIVE.common.JsonError;
import gov.inl.LIVE.common.JsonListResult;
import gov.inl.LIVE.common.Utility;
import gov.inl.LIVE.entity.PermissionGroup;
import java.io.IOException;
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
public class PermissionGroupController
{
    @Autowired
    ObjectMapper objMapper;
    
    @Autowired
    PermissionGroupDAO permissionGroupDAO;
    
    
    private String getHome(){ return "home"; }
    
    @RequestMapping(value = "/groups", method = RequestMethod.GET)
    public String getPermissionGroups() { return getHome(); }
    
    @RequestMapping(value = "/groups/", method = RequestMethod.GET)
    public String getPermissionGroupsWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/groups/edit/{id}", method = RequestMethod.GET)
    public String getPermissioGroupnById(){ return getHome(); }
    
    @RequestMapping(value = "/groups/create", method = RequestMethod.GET)
    public String getPermissionGroupCreate() { return getHome(); }
    
    
    
    @RequestMapping(value = "/api/permissiongroups/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getPermissionGroups(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "sortField", defaultValue = "") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(name = "multiSortMeta", defaultValue = "") String multiSortMeta,
            @RequestParam(name = "filters", defaultValue = "") String filters
            )
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<PermissionGroup,PermissionGroup> triple = permissionGroupDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<PermissionGroup> root = triple.getRoot();
        List<PermissionGroup> list = null;
        long total = 0;
        
        System.out.println("Start:" + start);
        System.out.println("Count:" + count);
        System.out.println("sortField:" + sortField);
        System.out.println("sortOrder:" + sortOrder);
        System.out.println("multiSortMeta:" + multiSortMeta);
        System.out.println("filters:" + filters);
        
        List<Predicate> predicateList = Utility.ProcessFilters(filters, cb, root, PermissionGroup.class, objMapper);
        
        CriteriaBuilderCriteriaQueryRootTriple<PermissionGroup,Long> tripleCount = permissionGroupDAO.getCriteriaTripleForCount();
        CriteriaBuilder cbCount = tripleCount.getCriteriaBuilder();
        Root<PermissionGroup> rootCount = tripleCount.getRoot();
        List<Predicate> predicateListCount = Utility.ProcessFilters(filters, cbCount, rootCount, PermissionGroup.class, objMapper);
        
        List<Order> orderList = Utility.ProcessOrders(sortField, sortOrder, cb, root, "groupName", objMapper);
        if (!predicateList.isEmpty())
        {
            list = permissionGroupDAO.findByCriteria(triple, orderList.toArray(new Order [0]), start, count,predicateList.toArray(new Predicate[0]));
            total = permissionGroupDAO.findByCriteriaCount(tripleCount,predicateListCount.toArray(new Predicate[0]));
            
        }
        else
        {
            list = permissionGroupDAO.getAll(triple, orderList.toArray(new Order [0]), start, count);
            total = permissionGroupDAO.getAllCount();
        }
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, list)), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/api/permissiongroups/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getPermissionGroupById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<PermissionGroup,PermissionGroup> triple = permissionGroupDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<PermissionGroup> root = triple.getRoot();
        
        
        PermissionGroup perm = permissionGroupDAO.findById(id);
        if (perm == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(objMapper.writeValueAsString(perm), HttpStatus.OK);
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/permissiongroups/{id}", method = RequestMethod.PUT, produces = "application/json"
            )
    public ResponseEntity<String> savePermissionGroup(@PathVariable(value = "id") long id,
                @RequestBody PermissionGroup group)
            throws JsonProcessingException, IOException
    {
        if (group == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<PermissionGroup,Long> triple = permissionGroupDAO.getCriteriaTripleForCount();
        Root<PermissionGroup> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
            Predicate pred = cb.and(cb.equal(root.get("groupName"), group.getGroupName()),
                                        cb.notEqual(root.get("id"), id));
        if (permissionGroupDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate group name")), HttpStatus.CONFLICT);
        
        
        permissionGroupDAO.saveOrUpdate(group);
        return new ResponseEntity<>(objMapper.writeValueAsString(group), HttpStatus.OK);
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/permissiongroups/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createPermissionGroup(@RequestBody PermissionGroup group)
            throws JsonProcessingException
    {   
        if (group == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<PermissionGroup,Long> triple = permissionGroupDAO.getCriteriaTripleForCount();
        Root<PermissionGroup> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Predicate pred = cb.equal(root.get("groupName"), group.getGroupName());
        if (permissionGroupDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate group name")), HttpStatus.CONFLICT);
        
        permissionGroupDAO.add(group);
        return new ResponseEntity<>(objMapper.writeValueAsString(group), HttpStatus.OK);
        
    }
    
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/permissiongroups/{id}", method = RequestMethod.DELETE, produces = "application/json"
            )
    public ResponseEntity<String> deletePermissionGroup(@PathVariable(value = "id") long id)
            throws JsonProcessingException, IOException
    {
        CriteriaBuilderCriteriaQueryRootTriple<PermissionGroup,Long> triple = permissionGroupDAO.getCriteriaTripleForCount();
        
        PermissionGroup group = permissionGroupDAO.findById(id);
        if (group == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        permissionGroupDAO.remove(group);
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



