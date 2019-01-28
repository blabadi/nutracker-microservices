package com.basharallabadi.nutracker.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;


@SpringBootApplication
@EnableAdminServer
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Bean
    public  OAuth2AuthorizationHttpHeadersProvider provider(OAuth2AuthorizedClientService clientService) {
        return new OAuth2AuthorizationHttpHeadersProvider(clientService);
    }



//    @Configuration
//    public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http
//                .csrf().disable();
//            http
//                .authorizeRequests()
//                .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
//                .permitAll();
//            http
//                .authorizeRequests()
//                .antMatchers("/**")
//                .authenticated();
//            http
//                .httpBasic();
//        }
//    }


}

