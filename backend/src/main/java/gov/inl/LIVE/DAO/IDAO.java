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
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;

public interface IDAO<T,K extends Serializable>
{
	
	public void add(T obj);
	public List<T> getAll(CriteriaBuilderCriteriaQueryRootTriple<T,T> triple,Order[] orders,int start, int maxSize);
	public long getAllCount();
	public T findById(K id);
	public List<T> findByCriteria(CriteriaBuilderCriteriaQueryRootTriple<T,T> triple, Order[] orders, int start, int maxSize, Predicate... preds);
	public long findByCriteriaCount(CriteriaBuilderCriteriaQueryRootTriple<T,Long> triple, Predicate... preds);
	public void remove(T obj);
	public void saveOrUpdate(T obj);
	public void refresh(T obj);
        public EntityManager getEntityManager();

}




