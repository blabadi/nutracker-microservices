package com.basharallabadi.nutracker.authserver;

import com.ea.async.Async;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerExchangeFilterFunction;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

@SpringBootApplication
@EnableDiscoveryClient
@RefreshScope
public class AuthServerApplication {

	public static void main(String[] args) {
		System.out.println("FIRST THING EVER");
		Async.init();
		SpringApplication.run(AuthServerApplication.class, args);
	}


	// this is needed to enable the webclient to use Eurka and Ribbon since @LoadBalanced not supported yet.
	@Bean
	WebClient client(LoadBalancerExchangeFilterFunction eff) {
		return WebClient.builder().filter(logResponse()).filter(eff).build();
	}

	//needed or this exception will be thrown:
	// https://stackoverflow.com/questions/46999940/spring-boot-passwordencoder-error
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


	private ExchangeFilterFunction logResponse() {
		return ExchangeFilterFunction.ofResponseProcessor(res -> {
			System.out.println("Response status code  " + res.statusCode());
			return Mono.just(res);
		});
	}
}

@Configuration
@EnableAuthorizationServer
class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	TokenStore tokenStore;

	@Autowired
	TokenEnhancer tokenEnhancer;

	//TODO: not good here,
	private static final String ENC_PASSWORD = "58347105";


	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("nutracker-app")
				.authorizedGrantTypes(
						"implicit", "refresh_token", "password", "authorization_code"
				)
				.authorities("READ", "WRITE")
				.scopes("profile", "entries")
				.resourceIds("api-gateway")
				.secret(encoder.encode("trusted"));
	}

	//https://stackoverflow.com/questions/28254519/spring-oauth2-authorization-server
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.tokenStore(tokenStore)
				.tokenEnhancer(tokenEnhancer)
				.authenticationManager(authenticationManager);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		KeyStoreKeyFactory keyStoreKeyFactory =
				new KeyStoreKeyFactory(
						new ClassPathResource("jwt.jks"),
						ENC_PASSWORD.toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair("jwt"));
		return converter;
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}


//    @Bean
//    public FilterRegistrationBean filterRegistrationBean() {
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        CorsConfiguration config = new CorsConfiguration();
//        config.setAllowCredentials(true);
//        config.addAllowedOrigin("*");
//        config.addAllowedHeader("*");
//        config.addAllowedMethod("*");
//        source.registerCorsConfiguration("/**", config);
//        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
//        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
//        return bean;
//    }
}


@Configuration
@EnableWebSecurity
class AuthenticationConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	IdentityServiceAdapter identityServiceAdapter;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder);
	}

	@Bean // <-- important for some fucking reason //TODO: figure why?
	@Override
	protected AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManager();
	}

	@Bean
	protected UserDetailsService userDetailsService() {
		return (username) -> {
			com.basharallabadi.nutracker.authserver.User user = Async.await(identityServiceAdapter.byUsername(username));
			return new User(
					user.getId(),
					user.getPassword(),
					true, true, true, true,
					AuthorityUtils.createAuthorityList("USER")
			);
		};
	}
}


