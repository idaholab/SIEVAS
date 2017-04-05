/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.SIEVAS.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import gov.inl.SIEVAS.common.IIdentifier;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author monejh
 */
@Entity
@Table(name = "user_info")
public class UserInfo implements Serializable, IIdentifier<Long>
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
    @Column(name = "username")
    private String username;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 512)
    @Column(name = "password")
    private String password;
    
    //needed for password validation, not persisted
    @Transient
    private String password2 = "";
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "first_name")
    private String firstName;
    
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 64)
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "edipi")
    private Long edipi;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "expired")
    private boolean expired;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "locked")
    private boolean locked;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "enabled")
    private boolean enabled;
    
    //@ManyToMany(mappedBy = "userInfoCollection", cascade = CascadeType.MERGE)
    @JoinTable(name = "user_info_permission_group", joinColumns =
    {
        @JoinColumn(name = "user_info_id", referencedColumnName = "id")
    }, inverseJoinColumns =
    {
        @JoinColumn(name = "permission_group_id", referencedColumnName = "id")
    })
    @ManyToMany
    private Collection<PermissionGroup> permissionGroupCollection;


    public UserInfo()
    {
    }

    public UserInfo(Long id)
    {
        this.id = id;
    }

    public UserInfo(Long id, String username, String password, String firstName, String lastName, boolean expired, boolean locked, boolean enabled)
    {
        this.id = id;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.expired = expired;
        this.locked = locked;
        this.enabled = enabled;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Long getEdipi()
    {
        return edipi;
    }

    public void setEdipi(Long edipi)
    {
        this.edipi = edipi;
    }

    public boolean getExpired()
    {
        return expired;
    }

    public void setExpired(boolean expired)
    {
        this.expired = expired;
    }

    public boolean getLocked()
    {
        return locked;
    }

    public void setLocked(boolean locked)
    {
        this.locked = locked;
    }

    public boolean getEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public String getPassword2()
    {
        return password2;
    }

    public void setPassword2(String password2)
    {
        this.password2 = password2;
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
        if (!(object instanceof UserInfo))
        {
            return false;
        }
        UserInfo other = (UserInfo) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "UserInfo[ id=" + id + ", username=" + username + " ]";
    }

    public Collection<PermissionGroup> getPermissionGroupCollection()
    {
        return permissionGroupCollection;
    }

    public void setPermissionGroupCollection(Collection<PermissionGroup> permissionGroupCollection)
    {
        this.permissionGroupCollection = permissionGroupCollection;
    }
    
}
