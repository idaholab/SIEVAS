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
package gov.inl.SIEVAS.adminconsole.permissiongroup;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.inl.SIEVAS.adminconsole.IIdentifier;
import gov.inl.SIEVAS.adminconsole.permission.Permission;
import gov.inl.SIEVAS.adminconsole.SIEVASSession;
import gov.inl.SIEVAS.adminconsole.user.UserInfo;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Entity
@Table(name = "permission_group")
@NamedQueries(
{
    @NamedQuery(name = "PermissionGroup.findAll", query = "SELECT p FROM PermissionGroup p"),
    @NamedQuery(name = "PermissionGroup.findById", query = "SELECT p FROM PermissionGroup p WHERE p.id = :id"),
    @NamedQuery(name = "PermissionGroup.findByGroupName", query = "SELECT p FROM PermissionGroup p WHERE p.groupName = :groupName"),
    @NamedQuery(name = "PermissionGroup.findByDescription", query = "SELECT p FROM PermissionGroup p WHERE p.description = :description")
})
public class PermissionGroup implements Serializable,  IIdentifier<Long>
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
    @Column(name = "group_name")
    private String groupName;
    
    @Size(max = 2147483647)
    @Column(name = "description")
    private String description;
    
    @JoinTable(name = "permission_group_permission", joinColumns =
    {
        @JoinColumn(name = "permission_group_id", referencedColumnName = "id")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "permission_id", referencedColumnName = "id")
    })
    @ManyToMany
    private Collection<Permission> permissionCollection;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "permissionGroupCollection")
    private Collection<UserInfo> userInfoCollection;
    
    @JsonIgnore
    @ManyToMany(mappedBy = "groups")
    private Collection<SIEVASSession> sessionCollection;

    public PermissionGroup()
    {
    }

    public PermissionGroup(Long id)
    {
        this.id = id;
    }

    public PermissionGroup(Long id, String groupName)
    {
        this.id = id;
        this.groupName = groupName;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getGroupName()
    {
        return groupName;
    }

    public void setGroupName(String groupName)
    {
        this.groupName = groupName;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Collection<Permission> getPermissionCollection()
    {
        return permissionCollection;
    }

    public void setPermissionCollection(Collection<Permission> permissionCollection)
    {
        this.permissionCollection = permissionCollection;
    }

    public Collection<UserInfo> getUserInfoCollection()
    {
        return userInfoCollection;
    }

    public void setUserInfoCollection(Collection<UserInfo> userInfoCollection)
    {
        this.userInfoCollection = userInfoCollection;
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
        if (!(object instanceof PermissionGroup))
        {
            return false;
        }
        PermissionGroup other = (PermissionGroup) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "PermissionGroup[ id=" + id + ", name=" + groupName 
                    + ", users: " + userInfoCollection + " ]";
    }
    
}
