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

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

public interface IDAO<T,K extends Serializable>
{
	
	public void add(T obj);
	public List<T> getAll(Order[] orders,int start, int maxSize);
	public int getAllCount();
	public T findById(K id);
	public List<T> findByCriteria(Criterion criterion,Order[] orders,int start, int maxSize);
	public int findByCriteriaCount(Criterion criterion);
	public List<T> findByCriteria(DetachedCriteria criteria,Order[] orders,int start, int maxSize);
	public int findByCriteriaCount(DetachedCriteria criteria);
	public void remove(T obj);
	public void saveOrUpdate(T obj);
	public void refresh(T obj);

}




