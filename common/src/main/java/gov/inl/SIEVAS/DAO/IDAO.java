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




