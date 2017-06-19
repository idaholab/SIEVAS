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
import gov.inl.SIEVAS.DAO.PermissionGroupDAO;
import gov.inl.SIEVAS.DAO.SIEVASSessionDAO;
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
import java.util.List;
import java.util.Objects;
import java.util.Random;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceUnit;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
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
    private static final String CONTROL_PREFIX = "control";
    private static final String DATA_PREFIX = "data";
    
    @Autowired
    private ObjectMapper objMapper;
    
    @Autowired
    private UserInfoDAO userInfoDAO;
    
    @Autowired
    private PermissionGroupDAO permGroupDAO;
    
    @Autowired
    private SIEVASSessionDAO sessionDAO;
    
    @Autowired
    private ActiveMQService amqService;
    
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
    
    //needed to generate random control/data streams
    private final Random random = new Random(System.currentTimeMillis());
    
    /***
     * Constructor for autowired controller.
     * @param userInfoDAO The user info DAO
     * @param entityManagerFactory The entity manager
     * @param amqService The AMQ Service bean.
     * @param objMapper The Jackson ObjectMapper bean.
     */
    @Autowired
    public SIEVASSessionController(UserInfoDAO userInfoDAO, SIEVASSessionDAO sessionDAO, EntityManagerFactory entityManagerFactory, ActiveMQService amqService, ObjectMapper objMapper)
    {
        
        this.userInfoDAO = userInfoDAO;
        this.sessionDAO = sessionDAO;
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
            EntityTransaction trans = entityManager.getTransaction();
            trans.begin();
            TransactionSynchronizationManager.bindResource(entityManagerFactory, new EntityManagerHolder(entityManager));
        }
    }

    /***
     * Attempts to unbind a entity manager for hibernate session.
     */
    public void unbindSession() {
        
        EntityManagerHolder emHolder = (EntityManagerHolder) TransactionSynchronizationManager
                .unbindResource(entityManagerFactory);
        emHolder.getEntityManager().getTransaction().commit();
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
        CriteriaBuilderCriteriaQueryRootTriple<SIEVASSession, SIEVASSession> triple = sessionDAO.getCriteriaTriple();
        List<SIEVASSession> list = sessionDAO.getAll(triple, null, 0, -1);
        for(SIEVASSession session: list)
        {
            
            session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
            AMQSessionInfo sessionInfo = amqService.addSession(session);
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
        CriteriaBuilderCriteriaQueryRootTriple<SIEVASSession, SIEVASSession> triple = sessionDAO.getCriteriaTriple();
        List<Order> orders = Utility.ProcessOrders(sortField, sortOrder, triple.getCriteriaBuilder(), triple.getRoot(), "name", objMapper);
        List<Predicate> preds = Utility.ProcessFilters(filters, triple.getCriteriaBuilder(), triple.getRoot(), SIEVASSession.class, objMapper);
        
        
        List<SIEVASSession> filteredList = sessionDAO.findByCriteria(triple, orders.toArray(new Order[0]), start, count, preds.toArray(new Predicate[0]));
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserInfo currentUser = Utility.getUserByUsername(username, userInfoDAO);
        for(int ii=filteredList.size()-1;ii>-1;ii--)
            if (!allowEdit(filteredList.get(ii), currentUser))
                filteredList.remove(ii);
        
        
        int total = filteredList.size();
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
        SIEVASSession session = sessionDAO.findById(id);  //sessionsMap.get(id);
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
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allowEdit(session, Utility.getUserByUsername(username, userInfoDAO)))
           return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Permission Denied")), HttpStatus.BAD_REQUEST);
        
        
        CriteriaBuilderCriteriaQueryRootTriple<SIEVASSession, Long> triple = sessionDAO.getCriteriaTripleForCount();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<SIEVASSession> root = triple.getRoot();
        Predicate pred = cb.and(cb.equal(root.get("name"), session.getName()),
                                        cb.notEqual(root.get("id"), id));
        long count = sessionDAO.findByCriteriaCount(triple, pred);
        if (count>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate session name")), HttpStatus.CONFLICT);
        
        amqService.updateSession(session);
        sessionDAO.saveOrUpdate(session);
        
        //sessionsMap.put(id, session);
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
        
        CriteriaBuilderCriteriaQueryRootTriple<SIEVASSession, Long> triple = sessionDAO.getCriteriaTripleForCount();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<SIEVASSession> root = triple.getRoot();
        Predicate pred = cb.equal(root.get("name"), session.getName());
        long count = sessionDAO.findByCriteriaCount(triple, pred);
        
        if (count>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate session name")), HttpStatus.CONFLICT);
        
        long rnd = random.nextLong();
        session.setControlStreamName(CONTROL_PREFIX + rnd);
        session.setDataStreamName(DATA_PREFIX + rnd);
        session.setActivemqUrl(this.amqService.getActiveMQClientUrl());
        
        sessionDAO.add(session);
        
        AMQSessionInfo sessionInfo = amqService.addSession(session);
        cleanSession(session);
        
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
        
        
        SIEVASSession session = sessionDAO.findById(id); //sessionsMap.get(id);
        if (session == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!allowEdit(session, Utility.getUserByUsername(username, userInfoDAO)))
           return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Permission Denied")), HttpStatus.BAD_REQUEST);
        
        amqService.removeSession(id);
        sessionDAO.remove(session);
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



