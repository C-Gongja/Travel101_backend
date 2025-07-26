package com.sharavel.sharavel_be.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sharavel.sharavel_be.auth.helper.CustomAuthorizationRequestResolver;
import com.sharavel.sharavel_be.auth.helper.OAuth2SuccessHandler;
import com.sharavel.sharavel_be.auth.service.CustomOAuth2UserService;
import com.sharavel.sharavel_be.security.jwt.JwtAuthEntryPoint;
import com.sharavel.sharavel_be.security.jwt.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	private final JwtAuthEntryPoint jwtAuthEntryPoint;
	private final JwtAuthFilter jwtAuthFilter;
	private final UserDetailsService userDetailsService;
	private final PasswordEncoder passwordEncoder;
	private final OAuth2SuccessHandler oAuth2SuccessHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final ClientRegistrationRepository clientRegistrationRepository;

	public SecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint, JwtAuthFilter jwtAuthFilter,
			UserDetailsService userDetailsService, PasswordEncoder passwordEncoder,
			OAuth2SuccessHandler oAuth2SuccessHandler, CustomOAuth2UserService customOAuth2UserService,
			ClientRegistrationRepository clientRegistrationRepository) {
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
		this.passwordEncoder = passwordEncoder;
		this.oAuth2SuccessHandler = oAuth2SuccessHandler;
		this.customOAuth2UserService = customOAuth2UserService;
		this.clientRegistrationRepository = clientRegistrationRepository;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable()) // CSRF disabled for dev env
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthEntryPoint))
				// Set session management to stateless why? this is jwt
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/oauth2/**").permitAll()
						.requestMatchers("/public/**").permitAll()
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/api/**").authenticated()
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.authorizationEndpoint(authz -> authz
								.authorizationRequestResolver(
										new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization")))
						.userInfoEndpoint(userInfo -> userInfo
								.userService(customOAuth2UserService))
						.successHandler(oAuth2SuccessHandler))
				// Configure AuthenticationManager with UserDetailsService and PasswordEncoder
				.authenticationManager(http.getSharedObject(
						org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder.class)
						.userDetailsService(userDetailsService)
						.passwordEncoder(passwordEncoder)
						.and()
						.build());
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		return http.build();
	}

	@Bean
	public OAuth2AuthorizationRequestResolver customAuthorizationRequestResolver(ClientRegistrationRepository repo) {
		return new CustomAuthorizationRequestResolver(repo, "/oauth2/authorization");
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
