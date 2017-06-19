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
import gov.inl.SIEVAS.DAO.DatasourceDAO;
import gov.inl.SIEVAS.DAO.UserInfoDAO;
import gov.inl.SIEVAS.common.JsonError;
import gov.inl.SIEVAS.common.JsonListResult;
import gov.inl.SIEVAS.common.Utility;
import gov.inl.SIEVAS.entity.DataSourceOption;
import gov.inl.SIEVAS.entity.Datasource;
import java.io.IOException;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Class for datasource controller
 * @author monejh
 */
@Controller
public class DatasourceController
{
    @Autowired
    ObjectMapper objMapper;
    
    @Autowired
    DatasourceDAO datasourceDAO;
    
    @Autowired
    UserInfoDAO userInfoDAO;
    
    
    /***
     * Gets the home URL.
     * @return The URL of the home page.
     */
    private String getHome(){ return Utility.getHomeURL(); }
    
    //Next four handle the various routes for angular 2.
    @RequestMapping(value = "/datasources", method = RequestMethod.GET)
    public String getDatasources() { return getHome(); }
    
    @RequestMapping(value = "/datasources/", method = RequestMethod.GET)
    public String getDatasourcesWithSlash() { return getHome(); }
    
    @RequestMapping(value = "/datasources/edit/{id}", method = RequestMethod.GET)
    public String getDatasourcesById(){ return getHome(); }
    
    @RequestMapping(value = "/datasources/create", method = RequestMethod.GET)
    public String getDatasourcesCreate() { return getHome(); }
    
    
    /***
     * Gets the listing of datasources
     * @param start The start row.
     * @param count The number of records to get.
     * @param sortField The field to sort on.
     * @param sortOrder The order of the sort. 1 = ascending, -1 = descending
     * @param multiSortMeta
     * @param filters
     * @return
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/api/datasources/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getDatasources(
            @RequestParam(name = "start", defaultValue = "0") int start,
            @RequestParam(name = "count", defaultValue = "10") int count,
            @RequestParam(name = "sortField", defaultValue = "") String sortField,
            @RequestParam(name = "sortOrder", defaultValue = "1") int sortOrder,
            @RequestParam(name = "multiSortMeta", defaultValue = "") String multiSortMeta,
            @RequestParam(name = "filters", defaultValue = "") String filters
            )
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<Datasource,Datasource> triple = datasourceDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<Datasource> root = triple.getRoot();
        List<Datasource> list = null;
        long total = 0;
        
        List<Predicate> predicateList = Utility.ProcessFilters(filters, cb, root, Datasource.class, objMapper);
        
        CriteriaBuilderCriteriaQueryRootTriple<Datasource,Long> tripleCount = datasourceDAO.getCriteriaTripleForCount();
        CriteriaBuilder cbCount = tripleCount.getCriteriaBuilder();
        Root<Datasource> rootCount = tripleCount.getRoot();
        List<Predicate> predicateListCount = Utility.ProcessFilters(filters, cbCount, rootCount, Datasource.class, objMapper);
        
        List<Order> orderList = Utility.ProcessOrders(sortField, sortOrder, cb, root, "name", objMapper);
        if (!predicateList.isEmpty())
        {
            list = datasourceDAO.findByCriteria(triple, orderList.toArray(new Order [0]), start, count,predicateList.toArray(new Predicate[0]));
            total = datasourceDAO.findByCriteriaCount(tripleCount,predicateListCount.toArray(new Predicate[0]));
            
        }
        else
        {
            list = datasourceDAO.getAll(triple, orderList.toArray(new Order [0]), start, count);
            total = datasourceDAO.getAllCount();
        }
        
        return new ResponseEntity<>(objMapper.writeValueAsString(new JsonListResult<>(total, list)), HttpStatus.OK);
    }
    
    /***
     * Gets a datasource by id.
     * @param id The id of the permission to get.
     * @return The datasource  JSON or the error result.
     * @throws JsonProcessingException 
     */
    @RequestMapping(value = "/api/datasources/{id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<String> getDatasourceById(@PathVariable(value = "id") long id)
            throws JsonProcessingException
    {
        
        CriteriaBuilderCriteriaQueryRootTriple<Datasource,Datasource> triple = datasourceDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<Datasource> root = triple.getRoot();
        
        
        Datasource ds = datasourceDAO.findById(id);
        if (ds == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(objMapper.writeValueAsString(ds), HttpStatus.OK);
        
    }
    
    /***
     * Updates a datasource values.
     * @param id The ID of the datasource to update.
     * @param perm The datasource's new values.
     * @return The updated datasource object or the error that occurred.
     * @throws JsonProcessingException
     * @throws IOException 
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/datasources/{id}", method = RequestMethod.PUT, produces = "application/json"
            )
    public ResponseEntity<String> saveDatasource(@PathVariable(value = "id") long id,
                @RequestBody Datasource ds)
            throws JsonProcessingException, IOException
    {
        if (ds == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<Datasource,Long> triple = datasourceDAO.getCriteriaTripleForCount();
        Root<Datasource> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
            Predicate pred = cb.and(cb.equal(root.get("name"), ds.getName()),
                                        cb.notEqual(root.get("id"), id));
        if (datasourceDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate datasource name")), HttpStatus.CONFLICT);
        
        //for(DataSourceOption opt: ds.getOptions())
        //    opt.setDatasource(ds);
        
        System.out.println("SAVING DS:" + ds);
        datasourceDAO.saveOrUpdate(ds);
        System.out.println("SAVED DS:" + ds);
        return new ResponseEntity<>(objMapper.writeValueAsString(ds), HttpStatus.OK);
        
    }
    
    /***
     * Creates a new datasource.
     * @param perm The datasource object to create.
     * @return The created datasource object or the error that occurred in
     *              processing.
     * @throws JsonProcessingException 
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/datasources/", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<String> createDatasource(@RequestBody Datasource ds)
            throws JsonProcessingException
    {   
        if (ds == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.BAD_REQUEST);
        
        CriteriaBuilderCriteriaQueryRootTriple<Datasource,Long> triple = datasourceDAO.getCriteriaTripleForCount();
        Root<Datasource> root = triple.getRoot();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Predicate pred = cb.equal(root.get("name"), ds.getName());
        if (datasourceDAO.findByCriteriaCount(triple, pred)>0)
            return new ResponseEntity<>(objMapper.writeValueAsString(new JsonError("Duplicate datasource name")), HttpStatus.CONFLICT);
        
        datasourceDAO.add(ds);
        return new ResponseEntity<>(objMapper.writeValueAsString(ds), HttpStatus.OK);
        
    }
    
    /***
     * Deletes a given datasource.
     * @param id The ID of the datasource to delete.
     * @return Nothing if no error, otherwise the error object.
     * @throws JsonProcessingException
     * @throws IOException 
     */
    @Transactional(readOnly = false)
    @RequestMapping(value = "/api/datasources/{id}", method = RequestMethod.DELETE, produces = "application/json"
            )
    public ResponseEntity<String> deleteDatasource(@PathVariable(value = "id") long id)
            throws JsonProcessingException, IOException
    {
        CriteriaBuilderCriteriaQueryRootTriple<Datasource,Long> triple = datasourceDAO.getCriteriaTripleForCount();
        
        Datasource ds = datasourceDAO.findById(id);
        if (ds == null)
            return new ResponseEntity<>(objMapper.writeValueAsString(""), HttpStatus.NOT_FOUND);
        
        datasourceDAO.remove(ds);
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
