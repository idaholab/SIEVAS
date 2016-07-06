/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.DAO;

/**
 *
 * @author monejh
 */

import gov.inl.LIVE.common.IIdentifier;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


public class AbstractDAO<T extends IIdentifier<K>,K extends Serializable> implements IDAO<T, K> {

	private Class<T> entityClass;
	
	 //@Autowired
	 //private SessionFactory sessionFactory;
        
        @Autowired
        @PersistenceContext
        private EntityManager entityManager;
        
	 public AbstractDAO(Class<T> entityClass) {
	        this.entityClass = entityClass;
	    }
	 
         
	 public Session getCurrentSession()
	 {
            return entityManager.unwrap(Session.class);
	 }
	 
	 @Override
	 public void add(T obj) {
		 
		 getCurrentSession().save(obj);
	 }

	 @Override	        
	 public List<T> getAll(Order[] orders, int start, int maxSize) {
		 Criteria criteria = getCurrentSession().createCriteria(entityClass);
		 if (orders!=null)
		 {
			 for(Order order: orders)
			 {
				 criteria.addOrder(order);
			 }
		 }
		 
		 criteria = criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		 criteria.setFirstResult(start);
		 if (maxSize>=0) criteria.setMaxResults(maxSize);
		 return (List<T>)criteria.list();
	 }
	 
	 @Override
	 public int getAllCount()
	 {
		 Criteria criteria = getCurrentSession().createCriteria(entityClass);
		 return ((Long)(criteria.setProjection(Projections.rowCount()).uniqueResult())).intValue();
	 }

	 @Override
	 public T findById(K id) {
		 return (T) getCurrentSession().get(entityClass, id);
	 }

	 @Override
	 public void remove(T obj) {
		 getCurrentSession().delete(obj);

	 }


	 @Override
	 public void saveOrUpdate(T obj) {
		 try{
			 getCurrentSession().update(obj);
		 }
		 catch(NonUniqueObjectException e){ obj = (T) getCurrentSession().merge(obj); getCurrentSession().update(obj);}

	 }
	 
	 @Override	 
	 public List<T> findByCriteria(Criterion criterion,Order[] orders,int start, int maxSize)
	 {
		 DetachedCriteria criteria =  DetachedCriteria.forClass(entityClass);
		 criteria.add(criterion);
		 return findByCriteria(criteria, orders, start, maxSize);
	        
	 }
	 
	 private DetachedCriteria copy(DetachedCriteria criteria) {
	        try {
	            ByteArrayOutputStream baostream = new ByteArrayOutputStream();
	            ObjectOutputStream oostream = new ObjectOutputStream(baostream);
	            oostream.writeObject(criteria);
	            oostream.flush();
	            oostream.close();
	            ByteArrayInputStream baistream = new ByteArrayInputStream(baostream.toByteArray());
	            ObjectInputStream oistream = new ObjectInputStream(baistream);
	            DetachedCriteria copy = (DetachedCriteria)oistream.readObject();
	            oistream.close();           
	            return copy;
	        } catch(Throwable t) {
	            throw new HibernateException(t);
	        }
	    }
	 
	 @Override	 
	 public List<T> findByCriteria(DetachedCriteria criteria,Order[] orders,int start, int maxSize)
	 {
		 Criteria crit =  copy(criteria).getExecutableCriteria(getCurrentSession());
		 if (orders!=null)
		 {
			 for(Order order: orders)
			 {
				 crit.addOrder(order);
			 }
		 }
		 
		 crit = crit.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		 crit.setFirstResult(start);
		 if (maxSize>=0) crit.setMaxResults(maxSize);
		 List list = (List<T>)crit.list();
		 return list;
	 }
	 
	 @Override
	 public int findByCriteriaCount(Criterion criterion)
	 {
		 DetachedCriteria criteria = DetachedCriteria.forClass(entityClass);
		 criteria.add(criterion);
		 return findByCriteriaCount(criteria);
	 }
	 
	 @Override
	 public int findByCriteriaCount(DetachedCriteria criteria)
	 {
		 
		 //System.out.println("CRIT");
		 //System.out.println(criteria);
		 //System.out.println(criteria.setProjection(Projections.rowCount()).uniqueResult());
		 //System.out.println(criteria.setProjection(Projections.rowCount()).uniqueResult().getClass().getName());
		 //System.out.println(((Long)criteria.setProjection(Projections.rowCount()).uniqueResult()).intValue());
		 Criteria crit =  copy(criteria).getExecutableCriteria(getCurrentSession());
		 int count = ((Long)(crit.setProjection(Projections.rowCount()).uniqueResult())).intValue();
		 //criteria.setProjection(null);
		 //criteria.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
		 return count;
	 }
	 
	 @Override
	 public void refresh(T obj)
	 {
	     getCurrentSession().refresh(obj);
	 }

}
