/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.DAO;


import gov.inl.LIVE.entity.Nbody;
import org.springframework.stereotype.Repository;

/**
 *
 * @author monejh
 */
@Repository
public class NbodyRepository extends AbstractDAO<Nbody, Long>
{
    protected NbodyRepository()
    {
        super(Nbody.class);
    }
}

