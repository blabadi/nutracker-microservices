package com.basharallabadi.nutracker.admin;


import de.codecentric.boot.admin.server.config.EnableAdminServer;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.StandardCharsets;
import java.util.Map;


@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }

    @Value("${oauth2.client.registration.admin-server.client-id}")
    String clientId;

    @Value("${oauth2.client.registration.admin-server.client-secret}")
    String clientSecret;

    @Value("${oauth2.client.provider.local-auth-server.token-uri}")
    String tokenUri;

    @Bean
    WebClient oauth2WebClient(LoadBalancerExchangeFilterFunction eff) {
        return WebClient.builder()
                .filter(eff)
                .filter((r, next) ->  {
//                    System.out.println(r.url() + " body : " + r.attributes() + " , " + r.headers());
                    return next.exchange(r);
                })
                .build();
    }

    @Bean
    @SuppressWarnings("unchecked")
    public HttpHeadersProvider jwtTokenInjector(WebClient oauth2client) {

        // #Note: I had to do the request here to make sure the token is initialized before the header provider is
        // passed around, if token wasn't initialized the gateway was showing down because the scheduled checker didn't
        // have the token.
        String basicAuthHeader = Base64Utils.encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));
        Map<String, String> jwt = oauth2client
                .post()
                .uri(tokenUri
                        + "?grant_type=client_credentials"
                        + "&client_id=" + clientId)
                .header(HttpHeaders.AUTHORIZATION, "Basic " + basicAuthHeader)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

//        System.out.println(jwt);
        String jwtToken = jwt.get("access_token");

        return (instance) -> {
            HttpHeaders headers = new HttpHeaders();

            if (instance.getRegistration().getName().equals("AUTH-SERVER")) {
                return headers;
            }

            if (jwtToken == null) {
                return headers;
            }

            headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
            return headers;
        };
    }
}

@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable();
        http
            .authorizeRequests()
            .antMatchers("/login.html", "/**/*.css", "/img/**", "/third-party/**")
            .permitAll();
        http
            .authorizeRequests()
            .antMatchers("/**")
            .authenticated();
        http
            .formLogin();
    }

}





