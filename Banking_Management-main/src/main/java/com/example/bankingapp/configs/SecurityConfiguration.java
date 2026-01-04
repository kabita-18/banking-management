package com.example.bankingapp.configs;

import com.example.bankingapp.exception.CustomAccessDeniedHandler;
import com.example.bankingapp.security.JwtAuthenticationFilter;
import com.example.bankingapp.security.JwtTokenProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    

    public SecurityConfiguration(UserDetailsService userDetailsService,
            JwtTokenProvider jwtTokenProvider) {
			this.userDetailsService = userDetailsService;
			this.jwtTokenProvider = jwtTokenProvider;
    	}
    
    @Bean
    public JwtAuthenticationFilter  jwtAuthenticationFilter() {
    	return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorizeRequests -> authorizeRequests
        		
        		//PUBLIC 
            .requestMatchers(
            		"/bankingapp/register",
            		"/bankingapp/login",
            		"/v3/api-docs/**", 
            		"/swagger-ui.html",
            		"/swagger-ui/**",
            		 "/bankingapp/login",
            		  "/bankingapp/register",
            		  "/bankingapp/refresh-token",
            		  "/bankingapp/logout"
            		).permitAll()
            
            // ADMIN ONLY
            .requestMatchers(
            		"/bankingapp",
            		"/bankingapp/admin/assign-role",
            		"/bankingapp/get_account_details_by_account_number/**",
            		"/bankingapp/create_account",
            		"bankingapp/update_account/**",
            		"bankingapp/delete_account/**",
            		"/bankingapp/deposit_money/**",
            		"/bankingapp/withdraw_money/**",
            		"/bankingapp/account_balance/**",
            		"bankingapp/transfer_money/**",
            		"/bankingapp/show_transaction_history/**",
            		"/bankingapp/download_transaction_history/**"
            		).hasRole("ADMIN")
            
            // AUTHENTICATED USERS
            .requestMatchers(
            		"/bankingapp/me",
            		"bankingapp/transfer_money/**",
            		"/bankingapp/deposit_money/**",
            		"/bankingapp/withdraw_money/**",
            		"/bankingapp/account_balance/**",
            		"/bankingapp/show_transaction_history/**",
            		"/bankingapp/download_transaction_history/**",
            		"/bankingapp/get_account_details_by_account_number/**"
            		)
                    .authenticated()
                    .anyRequest().denyAll()
            )
            .sessionManagement(sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(exception -> exception
            		.accessDeniedHandler(customAccessDeniedHandler))
            
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        System.out.println("Authentication4");
        return http.build();
    }
    
   

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder())
                   .and()
                   .build();
    }
}
