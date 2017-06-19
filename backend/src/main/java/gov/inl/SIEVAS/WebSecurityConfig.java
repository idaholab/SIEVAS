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
package gov.inl.SIEVAS;

import gov.inl.SIEVAS.common.CsrfHeaderFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/**
 * Class for security config for spring security
 * @author monejh
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    
    @Autowired
    private SimpleUrlAuthenticationSuccessHandler handler;
   
    /***
     * Configures the http security options
     * @param http
     * @throws Exception 
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.httpBasic().disable()
            .authorizeRequests()
                .antMatchers("/resources/angular2/login/**")
                .permitAll()
                .and()
            .formLogin()
                .loginPage("/login").successHandler(handler)
                .permitAll()
                .and()
            .logout()
                .permitAll()
                .invalidateHttpSession(true).clearAuthentication(true).deleteCookies()
                .and()
            .authorizeRequests()
                .antMatchers("/", "/menu", 
                        "/permissions","/permissions/**",
                        "/groups","/groups/**",
                        "/users","/users/**",
                        "/sessions","/sessions/**",
                        "/resources/**", "/api/**", "/logout")
                .authenticated()
                .and()
            
            .csrf().disable()
                //.csrfTokenRepository(csrfTokenRepository())
                //.and()
            .addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
                
            
    }
    
    /***
     * Gets the CsrfTokenRepo. Sets the correct header needed.
     * @return 
     */
    private CsrfTokenRepository csrfTokenRepository()
    {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setHeaderName("X-XSRF-TOKEN");
        return repository;
    }
    
    /***
     * Sets the global security to use our details service
     * @param auth The builder for authentication
     * @throws Exception 
     */
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }
    
    /***
     * Bean for the authentication provider.
     * @return The auth provider that uses the BCrypt encoder and the user details service.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

}
