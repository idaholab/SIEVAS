/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE.common;

import gov.inl.LIVE.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.LIVE.DAO.UserInfoDAO;
import gov.inl.LIVE.entity.Permission;
import gov.inl.LIVE.entity.PermissionGroup;
import gov.inl.LIVE.entity.UserInfo;
import gov.inl.LIVE.entity.UserInfo_;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


/**
 *
 * @author monejh
 */
@Component
public class LIVEUserDetailsService implements UserDetailsService
{
    @Autowired
    private UserInfoDAO userInfoDAO;
    
    @Transactional
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        CriteriaBuilderCriteriaQueryRootTriple<UserInfo,UserInfo> triple = userInfoDAO.getCriteriaTriple();
        CriteriaBuilder cb = triple.getCriteriaBuilder();
        CriteriaQuery<UserInfo> criteria = triple.getQuery();
        Root<UserInfo> root = triple.getRoot();
        
        Predicate pred = cb.equal(root.get(UserInfo_.username), username);
        
        List<UserInfo> list = userInfoDAO.findByCriteria(triple, null, 0, -1, pred);
        if (list==null)
            throw new UsernameNotFoundException("User not found");
        else if (list.isEmpty())
            throw new UsernameNotFoundException("User not found");
        else
        {
            //prefetch the lazy loaded relationships
            Hibernate.initialize(list.get(0).getPermissionGroupCollection());
            list.get(0).getPermissionGroupCollection().forEach((group)->
            {
                Hibernate.initialize(group.getPermissionCollection());
            });
            return new UserDetails() {
                @Override
                public Collection<? extends GrantedAuthority> getAuthorities() {
                    Collection<PermissionGroup> coll = list.get(0).getPermissionGroupCollection();
                    Collection<Permission> returnColl = new ArrayList<>();
                    coll.forEach((group)->
                    {
                        group.getPermissionCollection().forEach((perm)->
                        {
                            returnColl.add(perm);
                        });
                    });
                    return returnColl;
                }

                @Override
                public String getPassword() {
                    return list.get(0).getPassword();
                }

                @Override
                public String getUsername() {
                    return list.get(0).getUsername();
                }

                @Override
                public boolean isAccountNonExpired() {
                    return !list.get(0).getExpired();
                }

                @Override
                public boolean isAccountNonLocked() {
                    return !list.get(0).getLocked();
                }

                @Override
                public boolean isCredentialsNonExpired() {
                    return !list.get(0).getExpired();
                }

                @Override
                public boolean isEnabled() {
                    return list.get(0).getEnabled();
                }
            };
        }
    }
    
}
