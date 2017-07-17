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
package gov.inl.SIEVAS.common;

import gov.inl.SIEVAS.DAO.CriteriaBuilderCriteriaQueryRootTriple;
import gov.inl.SIEVAS.DAO.UserInfoDAO;
import gov.inl.SIEVAS.entity.Permission;
import gov.inl.SIEVAS.entity.PermissionGroup;
import gov.inl.SIEVAS.entity.UserInfo;
import gov.inl.SIEVAS.entity.UserInfo_;
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
 * Class to handle user authentication for Spring Security.
 * @author monejh
 */
@Component
public class SIEVASUserDetailsService implements UserDetailsService
{
    @Autowired
    private UserInfoDAO userInfoDAO;
    
    /***
     * Loads a user by username.
     * @param username The username to search.
     * @return The user found. Throws UsernameNotFoundException when not found and not null.
     * @throws UsernameNotFoundException 
     */
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
