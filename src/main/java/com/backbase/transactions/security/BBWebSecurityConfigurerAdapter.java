package com.backbase.transactions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Spring security class in charge of filtering any request to every endpoint
 * but for /status, for any user that is not authenticated.
 * 
 * @author Damian
 */
@Configuration
@PropertySource("classpath:application.properties")
@EnableWebSecurity
public class BBWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    @Autowired
    private BBBasicAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private Environment env;

    public BBWebSecurityConfigurerAdapter() {
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // TODO: we should store this values preferable in a database or
        // somewhere else, just left in that way for demonstration purposes.
        String user = env.getProperty("user");
        String password = env.getProperty("password");

        System.out.println("Reading user: " + user + " pass: " + password);
        
        auth.inMemoryAuthentication().withUser(user).password(password).authorities("ROLE_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/status").permitAll().anyRequest().authenticated().and().httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
    }
}
