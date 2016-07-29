package gov.inl.LIVE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


/***
 * Spring Boot application startup
 * @author monejh
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"gov.inl.LIVE"})
@EnableJpaRepositories(basePackages = {"gov.inl.LIVE"})
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebMvc
public class Application extends WebMvcAutoConfiguration
{
    /***
     * Main function for spring boot. Runs this class.
     * @param args 
     */
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
    
    /***
     * Bean for the password encrypter using BCrypt
     * @return BCryptPasswordEncoder for the encoder
     */
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    
}

