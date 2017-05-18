/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.inl.SIEVAS.common.DriverOption;
import gov.inl.SIEVAS.common.IDriver;
import gov.inl.SIEVAS.common.JsonError;
import gov.inl.SIEVAS.common.JsonListResult;
import gov.inl.SIEVAS.common.Utility;
import gov.inl.SIEVAS.entity.DriverInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.reflections.Reflections;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author monejh
 */
@Controller
public class DriverController
{
    @Autowired
    private ObjectMapper objMapper;
    
    private HashMap<String, DriverInfo> driverMap = new HashMap<>();
    

    private String getHome(){ return Utility.getHomeURL(); }
    
    @RequestMapping(value = "/drivers", method = RequestMethod.GET)
    public String getDrivers() { return getHome(); }
    
    @RequestMapping(value = "/drivers/", method = RequestMethod.GET)
    public String getDriversWithSlash() { return getHome(); }
    
    @PersistenceUnit
    private final EntityManagerFactory entityManagerFactory;
    
    /***
     * Constructor for autowired controller.
     * @param entityManagerFactory The entity manager
     * @param objMapper The Jackson ObjectMapper bean.
     */
    @Autowired
    public DriverController(EntityManagerFactory entityManagerFactory, ObjectMapper objMapper)
    {
        this.entityManagerFactory = entityManagerFactory;
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
        
        //bindSession();
        
        
        Reflections reflections = new Reflections("gov.inl.SIEVAS");
        Set<Class<? extends IDriver>> driverTypes = reflections.getSubTypesOf(IDriver.class);
        for(Class<? extends IDriver> driver : driverTypes)
        {
            DriverInfo info = new DriverInfo(driver.getPackage().getName(), driver.getSimpleName());
            driverMap.put(info.getPackageName() + "." + info.getDriverName(), info);
        }
        
        //unbindSession();
     
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
    @RequestMapping(value = "/api/drivers/", method = RequestMethod.GET, produces = "application/json")
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
        Collection<DriverInfo> list = driverMap.values();
        
        List<DriverInfo> filteredList = Utility.ProcessFilters(list, filters, DriverInfo.class, objMapper);
        
        int total = filteredList.size();
        Utility.ProcessOrders(filteredList.toArray(new DriverInfo [0]),DriverInfo.class, sortField, sortOrder, "driverName", objMapper);
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, filteredList)), HttpStatus.OK);
    }
    
    
    @RequestMapping(value = "/api/drivers/optionlist/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getDriverOptions(
            @RequestParam(name = "driver", defaultValue = "") String driver
    )
            throws JsonProcessingException
    {
        System.out.println("DRIVER = " + driver);
        List<DriverOption> options = new ArrayList<DriverOption>();
        try
        {
            Class<? extends IDriver> clazz = (Class<? extends IDriver>) Class.forName(driver);
            IDriver driverInstance = (IDriver)clazz.newInstance();
            options = (List<DriverOption>) clazz.getMethod("getOptionList").invoke(driverInstance);
            System.out.println("OPTS:" + options);
        } 
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (NoSuchMethodException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (SecurityException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (IllegalAccessException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (IllegalArgumentException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (InvocationTargetException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        catch (InstantiationException ex)
        {
            Logger.getLogger(DriverController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError(ex.getMessage())), HttpStatus.BAD_REQUEST);
        }
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(options.size(), options)), HttpStatus.OK);
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
