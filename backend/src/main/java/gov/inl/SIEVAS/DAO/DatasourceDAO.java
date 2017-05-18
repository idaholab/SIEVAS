/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.DAO;

import org.springframework.stereotype.Service;

/**
 * Repository for data sources
 * @author monejh
 */
@Service("datasourceDAO")
public class DatasourceDAO extends DatasourceRepository
{
    protected DatasourceDAO()
    {
        super();
    }
}
