package gov.inl.LIVE;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


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
//@EnableWebMvc
public class Application extends WebMvcConfigurerAdapter//WebMvcAutoConfiguration
{
    
    private static int HTTP_PORT = 8080;
    
    @Autowired 
    LogInterceptor logInterceptor;
    
    
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
    
    
    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
            TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
            tomcat.addAdditionalTomcatConnectors(createStandardConnector());
            return tomcat;
    }

    private Connector createStandardConnector() {
            Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
            connector.setPort(HTTP_PORT);
            return connector;
    }
    
    
    @Bean
    public SimpleUrlAuthenticationSuccessHandler getAuthHandler()
    {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setTargetUrlParameter("redirect");
        handler.setAlwaysUseDefaultTargetUrl(false);
        handler.setUseReferer(false);
        
        return handler;
    }
    
    
}

