package com.alan.filesystemchallenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(requests -> {
							try {
								requests
												// TODO this /file/upload should be secured
												.requestMatchers(HttpMethod.POST, "/user/create", "/file/upload").permitAll()
//												.requestMatchers("/login.html", "/login",  "/home", "/error").permitAll()
												.requestMatchers("/error").permitAll()
												.anyRequest().authenticated()
												.and().csrf().disable(); // TODO: this should be removed
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
				)
				.formLogin(form -> form
						.loginPage("/login.html")
						.loginProcessingUrl("/login")
		                .defaultSuccessUrl("/home", true)
						.failureUrl("/error")
						.permitAll()
				)
				.logout(LogoutConfigurer::permitAll);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService customUserDetailsService) {

		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		List<AuthenticationProvider> providers = List.of(authProvider);

		return new ProviderManager(providers);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
