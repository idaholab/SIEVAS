/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.DAO;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author monejh
 */
public class CriteriaBuilderCriteriaQueryRootTriple<T,Q>
{
    private CriteriaQuery<Q> query;
    private Root<T> root;
    private CriteriaBuilder criteriaBuilder;
    
    public CriteriaBuilderCriteriaQueryRootTriple(CriteriaBuilder criteriaBuilder, CriteriaQuery<Q> query, Root<T> root)
    {
        this.criteriaBuilder = criteriaBuilder;
        this.query = query;
        this.root = root;
    }

    public CriteriaBuilder getCriteriaBuilder()
    {
        return criteriaBuilder;
    }

    public void setCriteriaBuilder(CriteriaBuilder criteriaBuilder)
    {
        this.criteriaBuilder = criteriaBuilder;
    }
    
    

    public CriteriaQuery<Q> getQuery()
    {
        return query;
    }

    public void setQuery(CriteriaQuery<Q> query)
    {
        this.query = query;
    }

    public Root<T> getRoot()
    {
        return root;
    }

    public void setRoot(Root<T> root)
    {
        this.root = root;
    }
    
}
