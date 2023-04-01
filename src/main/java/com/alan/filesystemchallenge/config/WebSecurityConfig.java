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
										.requestMatchers("/error").permitAll()
										.requestMatchers(HttpMethod.POST, "/user/create").permitAll()
										.anyRequest().authenticated()
										// on a real project this should be enabled (and used csrf token to prevent csrf attacks)
										.and().csrf().disable();
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						}
				)
				.formLogin(form -> form
						.loginPage("/login.html")
						.loginProcessingUrl("/login")
		                .defaultSuccessUrl("/home", false)
						.failureUrl("/error")
						.permitAll()
				)
				.logout(LogoutConfigurer::permitAll);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService customUserDetailsService) {
		var authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());

		var providers = List.<AuthenticationProvider>of(authProvider);

		return new ProviderManager(providers);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
}
