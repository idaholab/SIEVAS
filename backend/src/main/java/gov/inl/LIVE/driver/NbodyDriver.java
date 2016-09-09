/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.driver;

import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.NbodyDAO;
import gov.inl.LIVE.DAO.NbodyInfoDAO;
import gov.inl.LIVE.common.IData;
import gov.inl.LIVE.common.IDriver;
import gov.inl.LIVE.entity.Nbody;
import gov.inl.LIVE.entity.NbodyInfo;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author monejh
 */
public class NbodyDriver implements IDriver
{

    private NbodyDAO nbodyDAO;
    private NbodyInfoDAO nbodyInfoDAO;
    private NbodyInfo nbodyInfo;
    private long nbodyInfoId = 1L;
    private CriteriaBuilderCriteriaQueryRootTriple<Nbody,Nbody> triple;
    private CriteriaBuilder cb;
    private Root<Nbody> root;
    private Order[] orderBy = new Order[2];
    
    @Override
    public void init(ApplicationContext context)
    {
        this.nbodyInfoDAO = context.getBean(NbodyInfoDAO.class);
        this.nbodyDAO = context.getBean(NbodyDAO.class);
        
        this.nbodyInfo = nbodyInfoDAO.findById(nbodyInfoId);
        
        triple = nbodyDAO.getCriteriaTriple();
        cb = triple.getCriteriaBuilder();
        root = triple.getRoot();
        orderBy[0] = cb.asc(root.get("step"));
        orderBy[1] = cb.asc(root.get("planetNumber"));
        
    }

    @Override
    public List getData(double startTime, double timestep, double resolution, long maxResults)
    {
        
        long startTimestep = (long) Math.floor(startTime/nbodyInfo.getTimestep());
        long stopTimestep = (long) Math.floor((startTime+timestep)/nbodyInfo.getTimestep());
        
        Predicate pred = cb.and(cb.ge(root.get("step"), startTimestep),cb.le(root.get("step"), stopTimestep));
        List<Nbody> list = nbodyDAO.findByCriteria(triple, orderBy, 0, -1, pred);
        
        //now run solution between each point
        //TODO
        //set time for DVR engine
        list.stream().forEach(nbody->{
            nbody.setTime(nbody.getStep()*nbodyInfo.getTimestep());
        });
        //DONE
        
        return list;
    }

    @Override
    public void shutdown()
    {
        
    }
    
}
