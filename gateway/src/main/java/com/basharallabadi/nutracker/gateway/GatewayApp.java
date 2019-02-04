package com.basharallabadi.nutracker.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.ISO_8859_1;

@SpringBootApplication
@RefreshScope
@RestController
@EnableDiscoveryClient
public class GatewayApp {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApp.class, args);
    }

    // For simple IP based rate limiting
    // https://windmt.com/2018/05/09/spring-cloud-15-spring-cloud-gateway-ratelimiter/
    @Bean(name = RemoteAddrKeyResolver.BEAN_NAME)
    public RemoteAddrKeyResolver remoteAddrKeyResolver() {
        return new RemoteAddrKeyResolver();
    }

    @Bean
    WebClient client(LoadBalancerExchangeFilterFunction eff) {
        return WebClient.builder().filter(eff).build();
    }

    @Configuration
    public class SecurityConfig {
        @Bean
        SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http)
                throws Exception {
            // @formatter:off
            http
                .csrf()
                    .disable()
                .authorizeExchange()
                    .pathMatchers("/actuator/**")
                    .hasAuthority("ACTUATOR")
                .and()
                .authorizeExchange()
                    .anyExchange()
                        .authenticated()
                .and()
                    .oauth2ResourceServer()
                        .jwt()
                            .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                            .publicKey(get());

            // @formatter:on
            return http.build();
        }
    }

    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        GrantedAuthoritiesExtractor extractor = new GrantedAuthoritiesExtractor();
        return new ReactiveJwtAuthenticationConverterAdapter(extractor);
    }

    static class GrantedAuthoritiesExtractor extends JwtAuthenticationConverter {
        protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
            List<String> all = new ArrayList<>();
            Collection<String> authorities =
                    jwt.getClaimAsStringList("authorities");
            if (authorities != null) {
                all.addAll(authorities);
            }

            authorities = jwt.getClaimAsStringList("scope");
            if (authorities != null) {
                all.addAll(authorities);
            }
            return all.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

    private RSAPublicKey get() throws Exception {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(new X509EncodedKeySpec(loadPEM()));
        return (RSAPublicKey) pub;
    }

    // https://stackoverflow.com/questions/11787571/how-to-read-pem-file-to-get-private-and-public-key/14177328
    // instead of using public key we can configure oauth2 authorization server with an issuer url for jwk
    // see: https://docs.spring.io/spring-security-oauth2-boot/docs/current-SNAPSHOT/reference/htmlsingle/#oauth2-boot-authorization-server-spring-security-oauth2-resource-server-jwk-set-uri
    // until spring security 5 provide support for reactive oauth2 authorization servers
    private byte[] loadPEM() throws IOException {
        Resource resource = new ClassPathResource("/public.cert");
        InputStream in = resource.getURL().openStream();
        String pem = new String(in.readAllBytes(), ISO_8859_1);
        Pattern parse = Pattern.compile("(?m)(?s)^---*BEGIN.*---*$(.*)^---*END.*---*$.*");
        String encoded = parse.matcher(pem).replaceFirst("$1");
        return Base64.getMimeDecoder().decode(encoded);
    }



// done in properties file to be configurable :D
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        //@formatter:off
//        return builder.routes()
//                .route("entries", r ->
//                        r.path("/api/v1/entry/**")
//                         .filters(fs -> fs
//                                 // https://stackoverflow.com/questions/48865174/spring-cloud-gateway-proxy-forward-the-entire-sub-part-of-url
//                                 //.rewritePath("\\/v1\\/entry\\/(?<path>.*)", "/entry/${path}")
//                                 .stripPrefix(2).prefixPath("/api"))
//                         .uri("lb://entries")
//                    )
//                .build();
//        //@formatter:on
//    }

// not needed but can be defined here and used in properties files like the key resolver
//    @Bean
//    RedisRateLimiter rateLimiter(@Value("${gateway.rateLimiter.defaultReplenishRate:40}") int defaultRate,
//                                 @Value("${gateway.rateLimiter.defaultBurstCapacity:80}") int defaultBurstCapacity) {
//        return new RedisRateLimiter(defaultRate, defaultBurstCapacity);
//    }



}


class RemoteAddrKeyResolver implements KeyResolver {
    public static final String BEAN_NAME = "remoteAddrKeyResolver";

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        return Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
    }

}