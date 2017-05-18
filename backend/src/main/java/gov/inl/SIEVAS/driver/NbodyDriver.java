/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.driver;

import gov.inl.SIEVAS.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.SIEVAS.DAO.NbodyDAO;
import gov.inl.SIEVAS.DAO.NbodyInfoDAO;
import gov.inl.SIEVAS.common.DriverOption;
import gov.inl.SIEVAS.common.IData;
import gov.inl.SIEVAS.common.IDriver;
import gov.inl.SIEVAS.connector.NBody;
import gov.inl.SIEVAS.entity.Nbody;
import gov.inl.SIEVAS.entity.NbodyInfo;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
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
    private NBodyGenerator generator = new NBodyGenerator();
    
    @Override
    public void init(ApplicationContext context, List<DriverOption> options)
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
        
        //Predicate pred = cb.and(cb.ge(root.get("step"), startTimestep),cb.le(root.get("step"), stopTimestep));
        //List<Nbody> list = nbodyDAO.findByCriteria(triple, orderBy, 0, -1, pred);
       
        //get the max step based on start
        CriteriaBuilder cb2 = nbodyDAO.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> query2 = cb2.createQuery(Long.class);
        Root<Nbody> root2 = query2.from(Nbody.class);
        query2.select(cb2.max(root2.get("step")));
        Predicate maxPred = cb2.le(root2.get("step"), startTimestep);
        query2.where(maxPred);
        Long maxStep = nbodyDAO.getEntityManager().createQuery(query2).getSingleResult();
        Predicate pred2 = cb.equal(root.get("step"), maxStep);
        List<Nbody> listPrior = nbodyDAO.findByCriteria(triple, orderBy, 0, -1, pred2);
        System.out.println("MAX:" + maxStep);
        listPrior.stream().forEachOrdered(nbody -> System.out.println(nbody));
        
        double startGenTime = maxStep*nbodyInfo.getTimestep();
        //double h = Math.pow(0.5, 14); //can change to 14 or 16 to change time step
        List<Nbody> results = generator.run(startGenTime, startTime+timestep, listPrior, nbodyInfo.getTimestep(), maxStep);
        
        //remove front steps not needed
        int stepsToSkip = (int)Math.floor((startTime - startGenTime)/nbodyInfo.getTimestep());
        System.out.println("SKIPPING:" + stepsToSkip);
        
        //results = results.subList(stepsToSkip*10, results.size());
        //now remove step smaller than resolution
        //TODO
        System.out.println("Computing final array");
        
        int itemsToSkip = (results.size()/10)/10;
        List<Nbody> list = new ArrayList<>((int)(results.size()/itemsToSkip)*10);
                System.out.println("LIST SIZE:" + (int)(results.size()/itemsToSkip)*10);
        System.out.println("RESULT SIZE:" + results.size());
        System.out.println("STARTING AT:" + (stepsToSkip*10));
        System.out.println("SKIPPING THESE:" + itemsToSkip);
        for(int jj=Math.max(0,stepsToSkip*10-1); jj<results.size(); jj+= itemsToSkip*10)
        {
            for(int ii=0;ii<10;ii++)
            {
                Nbody nbody = results.get(jj + ii);
                if (nbody!=null)
                {
                    //set time for DVR engine
                    nbody.setTime(nbody.getStep()*nbodyInfo.getTimestep());    
                    list.add(nbody);
                }
            }
            
        }
        //now run solution between each point
        //TODO
        
        //DONE
        
        return list;
    }
    
    @Override
    public void shutdown()
    {
        
    }
    
}
