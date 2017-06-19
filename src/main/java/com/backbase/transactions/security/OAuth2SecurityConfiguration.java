package com.backbase.transactions.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * Class that defines user roles, names and password in a given token store.
 * 
 * @author Damian
 */
@Configuration
@EnableWebSecurity
@PropertySource("classpath:application.properties")
public class OAuth2SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2SecurityConfiguration.class);

    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Autowired
    private Environment env;

    /**
     * Adds and define all the user names, roles and passwords to an in memory
     * authentication storage. Values are taken from application.properties.
     * 
     * @param auth
     *            auth builder to add all the user definitions.
     * @throws Exception
     *             Exception if an error occurs when adding the in memory
     *             authentication.
     */
    @Autowired
    public void createUserPermissions(AuthenticationManagerBuilder auth) throws Exception {
        String adminUsername = env.getProperty("admin_username");
        String username = env.getProperty("username");

        auth.inMemoryAuthentication().withUser(adminUsername).password(env.getProperty("admin_password"))
                .roles(ADMIN_ROLE).and().withUser(username).password(env.getProperty("user_password")).roles(USER_ROLE);

        LOGGER.debug("Initializing admin username: '{}' and username: '{}'", adminUsername, username);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Disable support for CSRF (Cross-site request forgery), and anonymous
        // user access when requesting for a token.
        http.csrf().disable().anonymous().disable().authorizeRequests().antMatchers("/oauth/token").permitAll();

        LOGGER.debug("Http Security Configuration: '{}'", http);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
    }

    @Bean
    @Autowired
    public TokenStoreUserApprovalHandler userApprovalHandler(TokenStore tokenStore) {
        TokenStoreUserApprovalHandler handler = new TokenStoreUserApprovalHandler();
        handler.setTokenStore(tokenStore);
        handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
        handler.setClientDetailsService(clientDetailsService);

        return handler;
    }

    @Bean
    @Autowired
    public ApprovalStore approvalStore(TokenStore tokenStore) throws Exception {
        TokenApprovalStore store = new TokenApprovalStore();
        store.setTokenStore(tokenStore);

        return store;
    }
}