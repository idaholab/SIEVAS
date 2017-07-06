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
package gov.inl.SIEVAS.adminconsole.permission;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import gov.inl.SIEVAS.adminconsole.IIdentifier;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author monejh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permission implements IIdentifier<Long>
{

    private static final long serialVersionUID = 1L;
    private Long id;
    
    private String permissionName;
    
    private String description;
    
    /*@JsonIgnore
    private Collection<PermissionGroup> permissionGroupCollection;
*/
    public Permission()
    {
    }

    public Permission(Long id)
    {
        this.id = id;
    }

    public Permission(Long id, String permissionName)
    {
        this.id = id;
        this.permissionName = permissionName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getPermissionName()
    {
        return permissionName;
    }

    public void setPermissionName(String permissionName)
    {
        this.permissionName = permissionName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    /*public Collection<PermissionGroup> getPermissionGroupCollection()
    {
        return permissionGroupCollection;
    }

    public void setPermissionGroupCollection(Collection<PermissionGroup> permissionGroupCollection)
    {
        this.permissionGroupCollection = permissionGroupCollection;
    }*/

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Permission))
        {
            return false;
        }
        Permission other = (Permission) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "Permission{id=" + id + ", name=" + permissionName + ", description=" + description + "}";
    }
    
}
