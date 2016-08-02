/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.DAO;

import gov.inl.LIVE.entity.PermissionGroup;
import org.springframework.stereotype.Repository;

/**
 *
 * @author monejh
 */
@Repository
public class PermissionGroupRepository extends AbstractDAO<PermissionGroup, Long>
{
    protected PermissionGroupRepository()
    {
        super(PermissionGroup.class);
    }
}
