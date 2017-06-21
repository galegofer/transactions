package com.galegofer.transactions.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.error.OAuth2AccessDeniedHandler;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

/**
 * Configures Resource and Authorization Servers for OAuth2 authentication.
 * 
 * @author Damian
 */
@Configuration
public class Oauth2ServerConfiguration {
    private static final String RESOURCE_ID = "transactions-api-id";

    // Same token store for Resource server and Auth one, some time it leads to
    // situations when access token is not recognized.
    private static InMemoryTokenStore tokenStore = new InMemoryTokenStore();

    /**
     * Configures the Resource Server parameters.
     * 
     * @author Damian
     */
    @EnableResourceServer
    public static class CustomResourceServerConfiguration extends ResourceServerConfigurerAdapter {
        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            resources.tokenStore(tokenStore).resourceId(RESOURCE_ID).stateless(false);
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // Protecting the API entry point, asking for authorization and
            // denying access for non recognized users,
            http.anonymous().disable().requestMatchers().antMatchers("/v1/current-accounts/**").and()
                    .authorizeRequests().antMatchers("/v1/current-accounts/**").access("hasRole('ADMIN')").and()
                    .exceptionHandling().accessDeniedHandler(new OAuth2AccessDeniedHandler());
        }
    }

    /**
     * Configures the Authorization Server parameters.
     * 
     * @author Damian
     */
    @Configuration
    @EnableAuthorizationServer
    @PropertySource("classpath:application.properties")
    public static class AuthenticationConfiguration extends AuthorizationServerConfigurerAdapter {
        private static String REALM = "TRANSACTIONS_REALM";

        @Autowired
        private UserApprovalHandler userApprovalHandler;

        @Autowired
        private AuthenticationManager authenticationManager;

        @Autowired
        private Environment env;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // defines an client id to allow it to login, with a given secret,
            // with the given authorities and scopes.
            // Provides Access Token TTL to 2 minutes and Refresh Token for 1
            // hour.
            clients.inMemory().withClient("transactions-client")
                    .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                    .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust").secret("secret")
                    .resourceIds(RESOURCE_ID)
                    .accessTokenValiditySeconds(Integer.valueOf(env.getProperty("access_token_validity")))
                    .refreshTokenValiditySeconds(Integer.valueOf(env.getProperty("refresh_token_validity")));
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // Using same tokenStore than Resource Server to avoid issues for
            // Access Tokens.
            endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler).tokenStore(tokenStore)
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
            oauthServer.realm(REALM + "/client");
        }
    }
}
