/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.entity;

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
public class Permission implements Serializable, GrantedAuthority
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
        return "gov.inl.LIVE.entity.Permission[ id=" + id + " ]";
    }

    @Override
    public String getAuthority()
    {
        return getPermissionName();
    }
    
}
