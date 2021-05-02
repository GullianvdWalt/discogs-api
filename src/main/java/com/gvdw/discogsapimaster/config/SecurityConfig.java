package com.gvdw.discogsapimaster.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * @author Gullian Van Der Walt
 * Created at 11:44 on May, 2021
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    // Allow unauthenticated access everywhere (we base authentication on sessionID).
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/**");
    }
}
