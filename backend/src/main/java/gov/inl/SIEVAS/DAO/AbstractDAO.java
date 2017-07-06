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
package gov.inl.SIEVAS.DAO;

/**
 *
 * @author monejh
 */
import gov.inl.SIEVAS.common.IIdentifier;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.NonUniqueObjectException;
import org.hibernate.PersistentObjectException;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractDAO<T extends IIdentifier<K>, K extends Serializable> implements IDAO<T, K>
{

    private final Class<T> entityClass;

    @Autowired
    @PersistenceContext
    private EntityManager entityManager;

    public AbstractDAO(Class<T> entityClass)
    {
        this.entityClass = entityClass;
    }

//    public Session getCurrentSession()
//    {
//        return entityManager.unwrap(Session.class);
//    }
    
    @Override
    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }
    
    

    @Override
    public void add(T obj)
    {
        getEntityManager().persist(obj);
    }

    @Override
    public List<T> getAll(CriteriaBuilderCriteriaQueryRootTriple<T,T> triple, Order[] orders, int start, int maxSize)
    {
        if (triple == null)
            triple = getCriteriaTriple();
        
        CriteriaQuery<T> criteria = triple.getQuery();
        Root<T> root = triple.getRoot();
        
        if (orders!=null)
            criteria.orderBy(orders);
        
        Query query = getEntityManager().createQuery(criteria);
        query.setFirstResult(start);
        
        if (maxSize >= 0)
        {
            query.setMaxResults(maxSize);
        }
        return (List<T>) query.getResultList();
    }

    @Override
    public long getAllCount()
    {
        CriteriaQuery<Long> criteria = entityManager.getCriteriaBuilder().createQuery(Long.class);
        criteria.select(entityManager.getCriteriaBuilder().count(criteria.from(entityClass)));
        //criteria.where(/*your stuff*/);
        Long result = entityManager.createQuery(criteria).getSingleResult();
        if (result == null)
            return 0;
        else
            return result;

    }

    @Override
    public T findById(K id)
    {
        return (T) getEntityManager().find(entityClass, id);
    }

    @Override
    public void remove(T obj)
    {
        getEntityManager().remove(obj);

    }

    @Override
    public void saveOrUpdate(T obj)
    {
        obj = (T) getEntityManager().merge(obj);
        getEntityManager().persist(obj);
        

    }
    
    /**
     *
     * @return
     */
    public CriteriaBuilderCriteriaQueryRootTriple<T,T> getCriteriaTriple()
    {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityClass);
        Root<T> root = query.from(entityClass);
        return new CriteriaBuilderCriteriaQueryRootTriple<T,T>(cb,query,root);
    }
    
    public CriteriaBuilderCriteriaQueryRootTriple<T,Long> getCriteriaTripleForCount()
    {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<T> root = query.from(entityClass);
        query.select(cb.count(root));
        
        return new CriteriaBuilderCriteriaQueryRootTriple<T,Long>(cb,query,root);
    }

    @Override
    public List<T> findByCriteria(CriteriaBuilderCriteriaQueryRootTriple<T,T> triple, Order[] orders, int start, int maxSize, Predicate... preds)
    {
        if (triple == null)
            triple= getCriteriaTriple();
        
        CriteriaQuery<T> criteria = triple.getQuery();
        Root<T> root = triple.getRoot();
        if (orders != null)
            criteria.orderBy(orders);
        
        criteria.select(root);
        if (preds!=null)
            criteria.where(preds);
        
        Query query = getEntityManager().createQuery(criteria);
        query.setFirstResult(start);
        if (maxSize >= 0)
            query.setMaxResults(maxSize);
        
        return (List<T>) query.getResultList();
        

    }
    
    
    @Override
    public long findByCriteriaCount(CriteriaBuilderCriteriaQueryRootTriple<T,Long> triple, Predicate... preds)
    {
        if (triple == null)
            triple = getCriteriaTripleForCount();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        Root<T> root = triple.getRoot();
        CriteriaQuery<Long> criteria = triple.getQuery();
        criteria.where(preds);
        Long result = entityManager.createQuery(criteria).getSingleResult();
        if (result == null)
            return 0;
        else
            return result;
        
    }

    
    @Override
    public void refresh(T obj)
    {
        getEntityManager().refresh(obj);
    }

}
