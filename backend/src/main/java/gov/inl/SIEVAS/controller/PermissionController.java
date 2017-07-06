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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.SIEVAS.DAO.PermissionDAO;
import gov.inl.SIEVAS.DAO.UserInfoDAO;
import gov.inl.SIEVAS.common.JsonError;
import gov.inl.SIEVAS.common.JsonListResult;
import gov.inl.SIEVAS.common.Utility;
import gov.inl.SIEVAS.entity.Permission;
import gov.inl.SIEVAS.entity.PermissionGroup;
import gov.inl.SIEVAS.entity.UserInfo;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * REST Controller for permissions
 * @author monejh
 */
@Controller
public class PermissionController
{
    @Autowired
    ObjectMapper objMapper;
    
    @Autowired
    PermissionDAO permissionDAO;
    
    @Autowired
    UserInfoDAO userInfoDAO;
    
    
    /***
     * Gets the home URL.
     * @return The URL of the home page.
     */
    private String getHome(){ return Utility.getHomeURL(); }
    
    //Next four handle the various routes for angular 2.
    @RequestMapping(value = "/permissions", method = RequestMethod.GET)
    public String getPermissions() { return getHome(); }
    
    @RequestMapping(value = "/permissions/", method = RequestMethod.GET)
    public String getPermissionsWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/permissions/edit/{id}", method = RequestMethod.GET)
    public String getPermissionById(){ return getHome(); }
    
    @RequestMapping(value = "/permissions/create", method = RequestMethod.GET)
    public String getPermissionCreate() { return getHome(); }
    
    
    /***
     * Gets the listing of permissions
     * @param start The start row.
     * @param count The number of records to get.
     * @param sortField The field to sort on.
     * @param sortOrder The order of the sort. 1 = ascending, -1 = descending
     * @param multiSortMeta
     * @param filters
     * @return
     * @throws JsonProcessingException 
     */
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
    
    /***
     * Gets a permission by id.
     * @param id The id of the permission to get.
     * @return The permission has JSON or the error result.
     * @throws JsonProcessingException 
     */
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
    
    /***
     * Gets a permission by id.
     * @param permission The permission name to get
     * @return returns true or false as a JSON object
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/api/permissions/user/{perm}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getUserHasPermission(@PathVariable(value = "perm") String permission)
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<Permission,Permission> triple = permissionDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<Permission> root = triple.getRoot();
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (StringUtils.isEmpty(username))
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        UserInfo currentUser = Utility.getUserByUsername(username, userInfoDAO);
        if (currentUser == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        for(PermissionGroup group: currentUser.getPermissionGroupCollection())
        {
            for(Permission perm: group.getPermissionCollection())
            {
                if (perm.getPermissionName().equals(permission))
                {
                    return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
    }
    
    /***
     * Updates a permissions values.
     * @param id The ID of the permission to update.
     * @param perm The permission's new values.
     * @return The updated permission object or the error that occurred.
     * @throws JsonProcessingException
     * @throws IOException 
     */
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
    
    /***
     * Creates a new permission.
     * @param perm The permission object to create.
     * @return The created permission object or the error that occurred in
     *              processing.
     * @throws JsonProcessingException 
     */
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
    
    /***
     * Deletes a given permission.
     * @param id The ID of the permission to delete.
     * @return Nothing if no error, otherwise the error object.
     * @throws JsonProcessingException
     * @throws IOException 
     */
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



