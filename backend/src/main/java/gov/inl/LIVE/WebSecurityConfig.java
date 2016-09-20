/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.inl.LIVE;

import gov.inl.LIVE.common.CsrfHeaderFilter;
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
