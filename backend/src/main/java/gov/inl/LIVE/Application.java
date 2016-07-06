package gov.inl.LIVE;

import java.io.IOException;
import javax.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {"gov.inl.LIVE"})
@EnableJpaRepositories(basePackages = {"gov.inl.LIVE"})
@EnableTransactionManagement
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableWebMvc
public class Application extends WebMvcAutoConfiguration
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
    
//    @Autowired
//    private EntityManagerFactory entityManagerFactory;
//
//    @Bean
//    public SessionFactory getSessionFactory() {
//        if (entityManagerFactory.unwrap(SessionFactory.class) == null)
//            throw new NullPointerException("factory is not a hibernate factory");
//        else
//            return entityManagerFactory.unwrap(SessionFactory.class);
//    }
    
    @Bean
    public BCryptPasswordEncoder getPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }
    
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry)
//    {
//        if (!registry.hasMappingForPattern("/resources/**"))
//            registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
//        if (!registry.hasMappingForPattern("/resources/angular2/**"))
//            registry.addResourceHandler("/resources/angular2/**").addResourceLocations("classpath:/resources/angular2/");
//    }
}

