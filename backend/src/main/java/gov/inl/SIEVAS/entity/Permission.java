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
package gov.inl.SIEVAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.inl.SIEVAS.common.IIdentifier;
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
import org.springframework.security.core.GrantedAuthority;

/**
 *
 * @author monejh
 */
@Entity
@Table(name = "permission")
@NamedQueries(
{
    @NamedQuery(name = "Permission.findAll", query = "SELECT p FROM Permission p"),
    @NamedQuery(name = "Permission.findById", query = "SELECT p FROM Permission p WHERE p.id = :id"),
    @NamedQuery(name = "Permission.findByPermissionName", query = "SELECT p FROM Permission p WHERE p.permissionName = :permissionName"),
    @NamedQuery(name = "Permission.findByDescription", query = "SELECT p FROM Permission p WHERE p.description = :description")
})
public class Permission implements Serializable, GrantedAuthority, IIdentifier<Long>
{

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "permission_name")
    private String permissionName;
    
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "permissionCollection")
    private Collection<PermissionGroup> permissionGroupCollection;

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

    public Collection<PermissionGroup> getPermissionGroupCollection()
    {
        return permissionGroupCollection;
    }

    public void setPermissionGroupCollection(Collection<PermissionGroup> permissionGroupCollection)
    {
        this.permissionGroupCollection = permissionGroupCollection;
    }

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

    @JsonIgnore
    @Override
    public String getAuthority()
    {
        return getPermissionName();
    }
    
}
