/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.DAO;


import gov.inl.SIEVAS.entity.NbodyInfo;
import org.springframework.stereotype.Repository;

/**
 *
 * @author monejh
 */
@Repository
public class NbodyInfoRepository extends AbstractDAO<NbodyInfo, Long>
{
    protected NbodyInfoRepository()
    {
        super(NbodyInfo.class);
    }
}
