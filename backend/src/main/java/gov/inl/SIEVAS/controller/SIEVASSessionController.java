/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.DAO.PermissionGroupDAO;
import gov.inl.SIEVAS.DAO.UserInfoDAO;
import gov.inl.SIEVAS.common.JsonError;
import gov.inl.SIEVAS.common.JsonListResult;
import gov.inl.SIEVAS.common.Utility;
import gov.inl.SIEVAS.entity.SIEVASSession;
import gov.inl.SIEVAS.entity.PermissionGroup;
import gov.inl.SIEVAS.entity.UserInfo;
import gov.inl.SIEVAS.service.AMQSessionInfo;
import gov.inl.SIEVAS.service.ActiveMQService;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * REST Controller for SIEVASSession information. In-memory.
 * @author monejh
 */
@Controller
public class SIEVASSessionController
{
    @Autowired
    private ObjectMapper objMapper;
    
    @Autowired
    private UserInfoDAO userInfoDAO;
    
    @Autowired
    private PermissionGroupDAO permGroupDAO;
    
    @Autowired
    private ActiveMQService amqService;
    
    private HashMap<Long, SIEVASSession> sessionsMap = new HashMap<>();
    

    private String getHome(){ return Utility.getHomeURL(); }
    
    @RequestMapping(value = "/sessions", method = RequestMethod.GET)
    public String getPermissions() { return getHome(); }
    
    @RequestMapping(value = "/sessions/", method = RequestMethod.GET)
    public String getPermissionsWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/sessions/edit/{id}", method = RequestMethod.GET)
    public String getPermissionById(){ return getHome(); }
    
    @RequestMapping(value = "/sessions/create", method = RequestMethod.GET)
    public String getPermissionCreate() { return getHome(); }
    
    @PersistenceUnit
    private final EntityManagerFactory entityManagerFactory;
    
    /***
     * Constructor for autowired controller.
     * @param userInfoDAO The user info DAO
     * @param entityManagerFactory The entity manager
     * @param amqService The AMQ Service bean.
     * @param objMapper The Jackson ObjectMapper bean.
     */
    @Autowired
    public SIEVASSessionController(UserInfoDAO userInfoDAO, EntityManagerFactory entityManagerFactory, ActiveMQService amqService, ObjectMapper objMapper)
    {
        this.userInfoDAO = userInfoDAO;
        this.entityManagerFactory = entityManagerFactory;
        this.amqService = amqService;
        this.objMapper = objMapper;
        setup();
    }
    
    
    /***
     * Attempts to bind a entity manager for hibernate session.
     */
    public void bindSession() {
        if (!TransactionSynchronizationManager.hasResource(entityManagerFactory)) {
            EntityManager entityManager = entityManagerFactory.createEntityManager();
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        }
    }

    /***
     * Attempts to unbind a entity manager for hibernate session.
     */
    public void unbindSession() {
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .unbindResource(entityManagerFactory);
        EntityManagerFactoryUtils.closeEntityManager(emHolder.getEntityManager());
    }
    
    /***
     * Sets up the initial session for user "user"
     */
    @Transactional
    private void setup()
    {
        //pattern for non-view related queries.
        /**
         * bindSession();
         * DO SOME WORK
         * unbindSession();
         */
        
        bindSession();
        
        UserInfo user = Utility.getUserByUsername("user", userInfoDAO);
        Utility.cleanUserInfo(user);
        Hibernate.initialize(user.getPermissionGroupCollection());
        for(PermissionGroup group: user.getPermissionGroupCollection())
        {
            Hibernate.initialize(group.getPermissionCollection());
        }
        SIEVASSession session = new SIEVASSession(1L, "test", user);
        session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
        try
        {
            createSIEVASSession(session);
        }
        catch(JsonProcessingException e)
        {
            StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                Logger.getLogger(SIEVASSessionController.class.getName()).log(Level.SEVERE, sw.toString());
        }
        
        unbindSession();
     
    }
    
    /***
     * Cleans the session of PII information.
     * @param session The session to clean
     */
    private void cleanSession(SIEVASSession session)
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
    
    /***
     * Gets a new session id.
     * @return The new id of the session.
     */
    synchronized private long generateNewSessionId()
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
    
    /***
     * Checks to see if the user has access to edit or join the session. The 
     *      user has access if the user is the owner, if the user is in the
     *      user list, or the user is in one of the groups in the group list.
     * @param session The session to check.
     * @param user The user to check against.
     * @return Returns true if the user has access, false if not.
     */
    private boolean allowEdit(SIEVASSession session, UserInfo user)
    {
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
            for(PermissionGroup group: session.getGroups())
            {
                if (group.getUserInfoCollection()!=null)
                    for(UserInfo user2: group.getUserInfoCollection())
                    {
                        if ((user2!=null) && (Objects.equals(user2.getId(), user.getId())))
                            return true;
                    }
            }
        }
        return false;
    }
    
    /***
     * Gets the listing of sessions that the user has access to.
     * @param start The starting row.
     * @param count The number of records to get
     * @param sortField The field to sort on.
     * @param sortOrder The order to sort. 1 = ascending, -1 = descending
     * @param multiSortMeta The multiSortMeta, which is not sent by PrimeNG 
     *                      currently.
     * @param filters The filters as a JSON string.
     * @return The response as a JSON object.
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/api/sessions/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getSIEVASSessions(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "sortField", defaultValue = "") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(name = "multiSortMeta", defaultValue = "") String multiSortMeta,
            @RequestParam(name = "filters", defaultValue = "") String filters
            )
            throws JsonProcessingException
    {
        Collection<SIEVASSession> list = sessionsMap.values();
        
        List<SIEVASSession> filteredList = Utility.ProcessFilters(list, filters, SIEVASSession.class, objMapper);
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfo currentUser = Utility.getUserByUsername(username, userInfoDAO);
        for(int ii=filteredList.size()-1;ii>-1;ii--)
            if (!allowEdit(filteredList.get(ii), currentUser))
                filteredList.remove(ii);
        
        int total = filteredList.size();
        Utility.ProcessOrders(filteredList.toArray(new SIEVASSession [0]),SIEVASSession.class, sortField, sortOrder, "name", objMapper);
        filteredList.stream().forEach((session) ->
        {
            cleanSession(session);
            session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
        });
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, filteredList)), HttpStatus.OK);
    }
    
    /***
     * Gets a session by id.
     * @param id The id of the session to retrieve.
     * @return The session if found, otherwise invalid request.
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/api/sessions/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getSIEVASSessionById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        SIEVASSession session = sessionsMap.get(id);
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
        {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            UserInfo currentUser = Utility.getUserByUsername(username, userInfoDAO);
            if (!allowEdit(session, currentUser))
                return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
            cleanSession(session);
            session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
                  System.out.println("CONTROL STREAM NAME:" + session.getControlStreamName());

            return new ResponseEntity<>(objMapper.writeValueAsString(session), HttpStatus.OK);
        }
        
    }
    
    /***
     * Saves an updated session. Requires all fields.
     * @param id The id of the session to update.
     * @param session The session values.
     * @return The updated record as JSON.
     * @throws JsonProcessingException
     * @throws IOException 
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/sessions/{id}", method = RequestMethod.PUT, produces = "application/json"
            )
    public ResponseEntity<String> saveSIEVASSession(@PathVariable(value = "id") long id,
                @RequestBody SIEVASSession session)
            throws JsonProcessingException, IOException
    {
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        for(PermissionGroup group: session.getGroups())
        {
            PermissionGroup group2 = permGroupDAO.findById(group.getId());
            Hibernate.initialize(group2.getUserInfoCollection());
            group.setUserInfoCollection(group2.getUserInfoCollection());
        }
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allowEdit(session, Utility.getUserByUsername(username, userInfoDAO)))
           return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Permission Denied")), HttpStatus.BAD_REQUEST);
        
        int count = 0;
        for(SIEVASSession chk: sessionsMap.values())
        {
            if (Objects.equals(chk.getName(), session.getName()) && (!Objects.equals(chk.getId(), session.getId())))
                count++;
        }
        if (count>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate session name")), HttpStatus.CONFLICT);
        
        
        sessionsMap.put(id, session);
        cleanSession(session);
        session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
        
        return new ResponseEntity<>(objMapper.writeValueAsString(session), HttpStatus.OK);
        
    }
    
    /***
     * Creates a new session. Includes new ID on result.
     * @param session The session to create.
     * @return The session with updated ID.
     * @throws JsonProcessingException 
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/sessions/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createSIEVASSession(@RequestBody SIEVASSession session)
            throws JsonProcessingException
    {   
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        int count = 0;
        session.setId(generateNewSessionId());
        for(SIEVASSession chk: sessionsMap.values())
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
        session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
        AMQSessionInfo sessionInfo = amqService.addSession(session.getId());
        session.setControlStreamName(sessionInfo.getControlTopicName());
  //     System.out.println("CONTROL STREAM NAME:" + session.getControlStreamName());
        session.setDataStreamName(sessionInfo.getDataTopicName());
        
        return new ResponseEntity<>(objMapper.writeValueAsString(session), HttpStatus.OK);
        
    }
    
    /***
     * Deletes the given session.
     * @param id The ID of the session to delete.
     * @return The result as REST status code. No result unless an error occurs.
     * @throws JsonProcessingException
     * @throws IOException 
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/sessions/{id}", method = RequestMethod.DELETE, produces = "application/json"
            )
    public ResponseEntity<String> deletePermission(@PathVariable(value = "id") long id)
            throws JsonProcessingException, IOException
    {
        
        
        SIEVASSession session = sessionsMap.get(id);
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allowEdit(session, Utility.getUserByUsername(username, userInfoDAO)))
           return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Permission Denied")), HttpStatus.BAD_REQUEST);
        
        amqService.removeSession(id);
        sessionsMap.remove(id);
        return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.OK);
        
    }
    
    /***
     * Handles any exceptions in processing and returns the error to the user
     *              for display.
     * @param req The request object.
     * @param exception The exception that occurred.
     * @return The JsonError object as REST/JSON.
     * @throws JsonProcessingException 
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(HttpServletRequest req, Exception exception) throws JsonProcessingException
    {
        //can print stack track for the user with this.
//        StringWriter sw = new StringWriter();
//        PrintWriter pw = new PrintWriter(sw);
//        exception.printStackTrace(pw);
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(exception.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}



