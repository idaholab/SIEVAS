/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.UserInfoDAO;
import gov.inl.LIVE.common.JsonError;
import gov.inl.LIVE.common.JsonListResult;
import gov.inl.LIVE.common.Utility;
import gov.inl.LIVE.entity.UserInfo;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
public class UserInfoController
{
    @Autowired
    ObjectMapper objMapper;
    
    @Autowired
    UserInfoDAO userInfoDAO;
    
    @Autowired
    BCryptPasswordEncoder encoder;
    
    
    private String getHome(){ return "home"; }
    
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsers() { return getHome(); }
    
    @RequestMapping(value = "/users/", method = RequestMethod.GET)
    public String getUsersWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/users/edit/{id}", method = RequestMethod.GET)
    public String getUserById(){ return getHome(); }
    
    @RequestMapping(value = "/users/create", method = RequestMethod.GET)
    public String getUserCreate() { return getHome(); }
    
    
    
    @RequestMapping(value = "/api/users/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getUsers(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "sortField", defaultValue = "") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(name = "multiSortMeta", defaultValue = "") String multiSortMeta,
            @RequestParam(name = "filters", defaultValue = "") String filters
            )
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,UserInfo> triple = userInfoDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<UserInfo> root = triple.getRoot();
        List<UserInfo> list = null;
        long total = 0;
        
        
        List<Predicate> predicateList = Utility.ProcessFilters(filters, cb, root, UserInfo.class, objMapper);
        
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,Long> tripleCount = userInfoDAO.getCriteriaTripleForCount();
        CriteriaBuilder cbCount = tripleCount.getCriteriaBuilder();
        Root<UserInfo> rootCount = tripleCount.getRoot();
        List<Predicate> predicateListCount = Utility.ProcessFilters(filters, cbCount, rootCount, UserInfo.class, objMapper);
        
        List<Order> orderList = Utility.ProcessOrders(sortField, sortOrder, cb, root, "username", objMapper);
        if (!predicateList.isEmpty())
        {
            list = userInfoDAO.findByCriteria(triple, orderList.toArray(new Order [0]), start, count,predicateList.toArray(new Predicate[0]));
            total = userInfoDAO.findByCriteriaCount(tripleCount,predicateListCount.toArray(new Predicate[0]));
            
        }
        else
        {
            list = userInfoDAO.getAll(triple, orderList.toArray(new Order [0]), start, count);
            total = userInfoDAO.getAllCount();
        }
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, list)), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getUserById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,UserInfo> triple = userInfoDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<UserInfo> root = triple.getRoot();
        
        
        UserInfo user = userInfoDAO.findById(id);
        if (user == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
        {
            user.setPassword("");
            return new ResponseEntity<>(objMapper.writeValueAsString(user), HttpStatus.OK);
        }
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.PUT, produces = "application/json"
            )
    public ResponseEntity<String> saveUser(@PathVariable(value = "id") long id,
                @RequestBody UserInfo user)
            throws JsonProcessingException, IOException
    {
        if (user == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        
        if (!user.getPassword().equals(user.getPassword2()))
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Passwords do not match")), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,Long> triple = userInfoDAO.getCriteriaTripleForCount();
        Root<UserInfo> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
            Predicate pred = cb.and(cb.equal(root.get("username"), user.getUsername()),
                                        cb.notEqual(root.get("id"), id));
        if (userInfoDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate user name")), HttpStatus.CONFLICT);
        
        if (!user.getPassword().equals(""))
            user.setPassword(encoder.encode(user.getPassword()));
        else
        {
            UserInfo oldUser = userInfoDAO.findById(id);
            if (oldUser != null)
                user.setPassword(oldUser.getPassword());
        }
        
        userInfoDAO.saveOrUpdate(user);
        return new ResponseEntity<>(objMapper.writeValueAsString(user), HttpStatus.OK);
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/users/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createUser(@RequestBody UserInfo user)
            throws JsonProcessingException
    {   
        if (user == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        if (!user.getPassword().equals(user.getPassword2()))
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Passwords do not match")), HttpStatus.CONFLICT);
        
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,Long> triple = userInfoDAO.getCriteriaTripleForCount();
        Root<UserInfo> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Predicate pred = cb.equal(root.get("username"), user.getUsername());
        if (userInfoDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate user name")), HttpStatus.CONFLICT);
        
        if (!user.getPassword().equals(""))
            user.setPassword(encoder.encode(user.getPassword()));
        
        userInfoDAO.add(user);
        return new ResponseEntity<>(objMapper.writeValueAsString(user), HttpStatus.OK);
        
    }
    
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/users/{id}", method = RequestMethod.DELETE, produces = "application/json"
            )
    public ResponseEntity<String> deleteUser(@PathVariable(value = "id") long id)
            throws JsonProcessingException, IOException
    {
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,Long> triple = userInfoDAO.getCriteriaTripleForCount();
        
        UserInfo user = userInfoDAO.findById(id);
        if (user == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        
        if (Objects.equals(user.getUsername(),SecurityContextHolder.getContext().getAuthentication().getName()))
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Cannot delete current user")), HttpStatus.BAD_REQUEST);
        
        userInfoDAO.remove(user);
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



