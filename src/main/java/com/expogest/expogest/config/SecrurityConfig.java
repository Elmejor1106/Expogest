package com.expogest.expogest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecrurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
				// Recursos públicos
				.requestMatchers("/", "/login", "/registro", "/css/**", "/js/**", "/images/**").permitAll()
				
				// Admin tiene acceso a todo
				.requestMatchers("/admin/**", "/usuarios/**").hasRole("ADMIN")
				
				// Organizador
				.requestMatchers("/organizador/**", "/eventos/**", "/stands/**", "/cronogramas/**").hasAnyRole("ADMIN", "ORGANIZADOR")
				.requestMatchers("/solicitudes", "/solicitudes/pendientes", "/solicitudes/evento/**").hasAnyRole("ADMIN", "ORGANIZADOR")
				.requestMatchers("/solicitudes/*/aprobar", "/solicitudes/*/rechazar").hasAnyRole("ADMIN", "ORGANIZADOR")
				
				// Expositor
				.requestMatchers("/expositor/**").hasAnyRole("ADMIN", "EXPOSITOR")
				.requestMatchers("/solicitudes/nueva/**", "/solicitudes/guardar", "/solicitudes/mis-solicitudes").hasAnyRole("ADMIN", "EXPOSITOR")
				.requestMatchers("/solicitudes/*/cancelar").hasAnyRole("ADMIN", "EXPOSITOR")
				
				// Evaluador
				.requestMatchers("/evaluador/**", "/evaluaciones/**").hasAnyRole("ADMIN", "EVALUADOR")
				
				// Visitante
				.requestMatchers("/visitante/**", "/participaciones/**").hasAnyRole("ADMIN", "VISITANTE")
				
				// Cualquier otra petición requiere autenticación
				.anyRequest().authenticated()
			)
			.formLogin(form -> form
				.loginPage("/login")
				.permitAll()
				.defaultSuccessUrl("/", true)
			)
			.logout(logout -> logout
				.logoutUrl("/logout")
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			);
		return http.build();
	}
}
