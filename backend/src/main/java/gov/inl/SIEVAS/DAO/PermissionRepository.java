/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.DAO;

import gov.inl.SIEVAS.entity.Permission;
import org.springframework.stereotype.Repository;

/**
 *
 * @author monejh
 */
@Repository
public class PermissionRepository extends AbstractDAO<Permission, Long>
{
    protected PermissionRepository()
    {
        super(Permission.class);
    }
}
