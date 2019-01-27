package com.basharallabadi.nutracker.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
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

