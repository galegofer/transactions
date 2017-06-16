package com.backbase.transactions.security;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
@EnableWebSecurity
public class BBWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(BBWebSecurityConfigurerAdapter.class);

    @Autowired
    private BBBasicAuthenticationEntryPoint authenticationEntryPoint;
    private Properties properties;

    public BBWebSecurityConfigurerAdapter() {
        try {
            properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/application.properties"));
        } catch (IOException e) {
            LOGGER.error("Error while trying to laod user names and passwords");
        }
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        // TODO: we should store this values preferable in a database or
        // somewhere else, just left in that way for demonstration purposes.
        String user = properties.getProperty("user");
        String password = properties.getProperty("password");

        auth.inMemoryAuthentication().withUser(user).password(password).authorities("ROLE_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/status").permitAll().anyRequest().authenticated().and().httpBasic()
                .authenticationEntryPoint(authenticationEntryPoint);
    }
}
