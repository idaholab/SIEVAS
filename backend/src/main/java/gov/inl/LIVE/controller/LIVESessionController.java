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
import gov.inl.LIVE.DAO.PermissionGroupDAO;
import gov.inl.LIVE.DAO.UserInfoDAO;
import gov.inl.LIVE.common.JsonError;
import gov.inl.LIVE.common.JsonListResult;
import gov.inl.LIVE.common.Utility;
import gov.inl.LIVE.entity.LIVESession;
import gov.inl.LIVE.entity.Permission;
import gov.inl.LIVE.entity.PermissionGroup;
import gov.inl.LIVE.entity.UserInfo;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;
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
public class LIVESessionController
{
    @Autowired
    private ObjectMapper objMapper;
    
    @Autowired
    private UserInfoDAO userInfoDAO;
    
    @Autowired
    private UserInfoController userInfoController;
    
    @Autowired PermissionGroupDAO permGroupDAO;
    
    private HashMap<Long, LIVESession> sessionsMap = new HashMap<>();

    private String getHome(){ return "home"; }
    
    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public String getPermissions() { return getHome(); }
    
    @RequestMapping(value = "/sessions/", method = RequestMethod.GET)
    public String getPermissionsWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/sessions/edit/{id}", method = RequestMethod.GET)
    public String getPermissionById(){ return getHome(); }
    
    @RequestMapping(value = "/sessions/create", method = RequestMethod.GET)
    public String getPermissionCreate() { return getHome(); }
    
    @PersistenceUnit
    private EntityManagerFactory entityManagerFactory;
    
    @Autowired
    public LIVESessionController(UserInfoDAO userInfoDAO, EntityManagerFactory emf)
    {
        this.userInfoDAO = userInfoDAO;
        this.entityManagerFactory = emf;
        setup();
    }
    
    

    public void bindSession() {
        if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        }
    }

    public void unbindSession() {
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .unbindResource(entityManagerFactory);
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
    
    @Transactional
    private void setup()
    {
        bindSession();
        
        UserInfo user = Utility.getUserByUsername("user", userInfoDAO);
        Utility.cleanUserInfo(user);
        Hibernate.initialize(user.getPermissionGroupCollection());
        for(PermissionGroup group: user.getPermissionGroupCollection())
        {
            Hibernate.initialize(group.getPermissionCollection());
        }
        LIVESession session = new LIVESession(1L, "test", user);
        sessionsMap.put(session.getId(), session);
        
        unbindSession();
     
    }
    
    private void cleanSession(LIVESession session)
    {
        if (session.getUsers()!=null)
        {
            session.getUsers().stream().forEach((user) ->
            {
                Utility.cleanUserInfo(user);
            });
        }
        if (session.getOwner()!=null)
            Utility.cleanUserInfo(session.getOwner());
    }
    
    private long generateNewSessionId()
    {
        long id = 1L;
        
        for(;id>0;id++)
        {
            if (id== Long.MAX_VALUE)
                return -1;
            if (!sessionsMap.containsKey(id))
                break;
        }
        return id;
    }
    
    private boolean allowEdit(LIVESession session, UserInfo user)
    {
        System.out.println("allowEdit");
        System.out.println(session);
        System.out.println(user);
        if (session == null)
            return false;
        if ((session.getOwner() == null) || (user == null))
            return false;
        if (Objects.equals(session.getOwner().getId(),user.getId()))
            return true;
        if (session.getUsers()!=null)
        {
            if (session.getUsers().stream().anyMatch((user1) -> (Objects.equals(user.getId(), user1.getId()))))
            {
                return true;
            }
            
        }
        if (session.getGroups()!=null)
        {
            System.out.println("Checking groups");
            for(PermissionGroup group: session.getGroups())
            {
                System.out.println("Checking group:" + group);
                if (group.getUserInfoCollection()!=null)
                    for(UserInfo user2: group.getUserInfoCollection())
                    {
                        System.out.println("Checking group user:" + user2.getUsername());
                        if ((user2!=null) && (Objects.equals(user2.getId(), user.getId())))
                            return true;
                    }
            }
        }
        return false;
    }
    
    @RequestMapping(value = "/api/sessions/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getLIVESessions(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "sortField", defaultValue = "") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(name = "multiSortMeta", defaultValue = "") String multiSortMeta,
            @RequestParam(name = "filters", defaultValue = "") String filters
            )
            throws JsonProcessingException
    {
        Collection<LIVESession> list = sessionsMap.values();
        
        List<LIVESession> filteredList = Utility.ProcessFilters(list, filters, LIVESession.class, objMapper);
        int total = filteredList.size();
        Utility.ProcessOrders(filteredList.toArray(new LIVESession [0]),LIVESession.class, sortField, sortOrder, "name", objMapper);
        for(LIVESession session: filteredList)
            cleanSession(session);
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, list)), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/api/sessions/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getLIVESessionById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        LIVESession session = sessionsMap.get(id);
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
        {
            cleanSession(session);
            return new ResponseEntity<>(objMapper.writeValueAsString(session), HttpStatus.OK);
        }
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/sessions/{id}", method = RequestMethod.PUT, produces = "application/json"
            )
    public ResponseEntity<String> saveLIVESession(@PathVariable(value = "id") long id,
                @RequestBody LIVESession session)
            throws JsonProcessingException, IOException
    {
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allowEdit(session, Utility.getUserByUsername(username, userInfoDAO)))
           return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Permission Denied")), HttpStatus.BAD_REQUEST);
        
        int count = 0;
        for(LIVESession chk: sessionsMap.values())
        {
            if (Objects.equals(chk.getName(), session.getName()) && (!Objects.equals(chk.getId(), session.getId())))
                count++;
        }
        if (count>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate session name")), HttpStatus.CONFLICT);
        
        for(PermissionGroup group: session.getGroups())
        {
            PermissionGroup group2 = permGroupDAO.findById(group.getId());
            Hibernate.initialize(group2.getUserInfoCollection());
            group.setUserInfoCollection(group2.getUserInfoCollection());
        }
        sessionsMap.put(id, session);
        cleanSession(session);
        
        
        return new ResponseEntity<>(objMapper.writeValueAsString(session), HttpStatus.OK);
        
    }
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/sessions/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createLIVESession(@RequestBody LIVESession session)
            throws JsonProcessingException
    {   
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        int count = 0;
        session.setId(generateNewSessionId());
        for(LIVESession chk: sessionsMap.values())
        {
            if (Objects.equals(chk.getName(), session.getName()) && (!Objects.equals(chk.getId(), session.getId())))
                count++;
        }
        if (count>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate session name")), HttpStatus.CONFLICT);
        
        for(PermissionGroup group: session.getGroups())
        {
            PermissionGroup group2 = permGroupDAO.findById(group.getId());
            Hibernate.initialize(group2.getUserInfoCollection());
            group.setUserInfoCollection(group2.getUserInfoCollection());
        }
        sessionsMap.put(session.getId(), session);
        cleanSession(session);
        
        return new ResponseEntity<>(objMapper.writeValueAsString(session), HttpStatus.OK);
        
    }
    
    
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/sessions/{id}", method = RequestMethod.DELETE, produces = "application/json"
            )
    public ResponseEntity<String> deletePermission(@PathVariable(value = "id") long id)
            throws JsonProcessingException, IOException
    {
        
        
        LIVESession session = sessionsMap.get(id);
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allowEdit(session, Utility.getUserByUsername(username, userInfoDAO)))
           return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Permission Denied")), HttpStatus.BAD_REQUEST);
        
        sessionsMap.remove(id);
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



