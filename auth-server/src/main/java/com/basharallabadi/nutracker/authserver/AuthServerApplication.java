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
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
import java.util.Map;

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
		return WebClient.builder().filter(logResponse()).filter(logRequest()).filter(eff).build();
	}

	//needed or this exception will be thrown:
	// https://stackoverflow.com/questions/46999940/spring-boot-passwordencoder-error
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	private ExchangeFilterFunction logRequest() {
		return ExchangeFilterFunction.ofRequestProcessor(req -> {
			System.out.println("Response status code  " + req.url());
			return Mono.just(req);
		});
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

	//TODO: not good here
	private static final String ENC_PASSWORD = "58347105";


	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("nutracker-app")
					.authorizedGrantTypes(
							"implicit", "refresh_token", "password", "authorization_code"
					)
	//				.authorities("READ", "WRITE")
					.scopes("PROFILE", "ENTRIES", "ACTUATOR")
					.resourceIds("api-gateway")
					.secret(encoder.encode("trusted"))
				.and()
					.withClient("admin-ui")
					.authorizedGrantTypes("authorization_code", "client_credentials")
					.secret(encoder.encode("admin-ui"))
//					.resourceIds("admin-data")
					.scopes("ACTUATOR", "PROFILE")
					.redirectUris("http://admin:9009/login/oauth2/code/admin-server");
//					.autoApprove(true);
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

	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/actuator/**")
					.permitAll()
			.anyRequest()
				.authenticated()
			.and()
			.	formLogin()
			.and()
				.httpBasic();
	}
	@Bean
	protected UserDetailsService userDetailsService() {
		return (username) -> {
			com.basharallabadi.nutracker.authserver.User user = Async.await(identityServiceAdapter.byUsername(username).toFuture());
			return new User(
					user.getId(),
					user.getPassword(),
					true, true, true, true,
					AuthorityUtils.createAuthorityList(user.getRoles().toArray(new String[0]))
			);
		};
	}
}

@RestController
class ProfileRestController {
	@GetMapping("/resources/user-info")
	Map<String, String> profile(Principal principal) {
	    return Collections.singletonMap("name", principal.getName());
	}
}

@Configuration
@EnableResourceServer
class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
			.antMatcher("/resources/**")
			.authorizeRequests()
			.mvcMatchers("/resources/user-info").access("#oauth2.hasScope('PROFILE')");
	}
}