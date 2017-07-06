/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.DAO;

import gov.inl.SIEVAS.entity.SIEVASSession;
import java.util.List;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import org.springframework.stereotype.Service;

/**
 *
 * @author monejh
 */

@Service("sievasSessionDAO")
public class SIEVASSessionDAO extends SIEVASSessionRepository
{
    
    protected SIEVASSessionDAO()
    {
        super();
    }
    
}
