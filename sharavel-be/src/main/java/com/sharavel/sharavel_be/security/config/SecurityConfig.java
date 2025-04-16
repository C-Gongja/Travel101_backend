package com.sharavel.sharavel_be.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.sharavel.sharavel_be.auth.helper.OAuth2SuccessHandler;
import com.sharavel.sharavel_be.auth.service.CustomOAuth2UserService;
import com.sharavel.sharavel_be.security.jwt.JwtAuthEntryPoint;
import com.sharavel.sharavel_be.security.jwt.JwtAuthFilter;
import com.sharavel.sharavel_be.security.util.JwtUtil;
import com.sharavel.sharavel_be.user.repository.RoleRepository;
import com.sharavel.sharavel_be.user.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtAuthEntryPoint jwtAuthEntryPoint;
	private final JwtAuthFilter jwtAuthFilter;
	private final UserDetailsService userDetailsService;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final JwtUtil jwtUtil;

	public SecurityConfig(JwtAuthEntryPoint jwtAuthEntryPoint, JwtAuthFilter jwtAuthFilter,
			UserDetailsService userDetailsService, UserRepository userRepository, RoleRepository roleRepository,
			JwtUtil jwtUtil) {
		this.jwtAuthEntryPoint = jwtAuthEntryPoint;
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.jwtUtil = jwtUtil;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable()) // 개발 시 CSRF 보호를 비활성화
				.exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(jwtAuthEntryPoint))
				// Set session management to stateless why? this is jwt
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/test/**").permitAll()
						.requestMatchers("/oauth2/**").permitAll()
						.requestMatchers("/public/**").permitAll()
						.requestMatchers("/auth/**").permitAll()
						.requestMatchers("/api/**").authenticated()
						.anyRequest().authenticated())
				.oauth2Login(oauth2 -> oauth2
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService()))
						.successHandler(oAuth2SuccessHandler()));
		http.authenticationProvider(authenticationProvider()); // Custom authentication provider
		http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CustomOAuth2UserService customOAuth2UserService() {
		return new CustomOAuth2UserService(userRepository, roleRepository);
	}

	@Bean
	public OAuth2SuccessHandler oAuth2SuccessHandler() {
		return new OAuth2SuccessHandler(jwtUtil, userRepository);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowCredentials(true);
		configuration.setAllowedOrigins(List.of("http://localhost:3000"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}
