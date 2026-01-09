package com.SpringBootMVC.ExpensesTracker.security;

import com.SpringBootMVC.ExpensesTracker.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider(UserService userService) {
                DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
                auth.setUserDetailsService(userService);
                auth.setPasswordEncoder(passwordEncoder());
                return auth;
        }

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        @Qualifier("customeAuthenticationSuccessHandler") AuthenticationSuccessHandler customAuthenticationSuccessHandler,
                        OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) throws Exception {

                http.authorizeHttpRequests(config -> config
                                .requestMatchers("/css/**", "/js/**", "/assets/**").permitAll()
                                .requestMatchers("/", "/showLoginPage",
                                                "/showRegistrationForm", "/processRegistration")
                                .permitAll()

                                // 🔥 ADD THESE
                                .requestMatchers(
                                                "/showAdd",
                                                "/submitAdd",
                                                "/list",
                                                "/showUpdate",
                                                "/submitUpdate",
                                                "/delete",
                                                "/processFilter")
                                .authenticated()

                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/showLoginPage")
                                                .loginProcessingUrl("/authenticateTheUser")
                                                .successHandler(customAuthenticationSuccessHandler)
                                                .permitAll())
                                // 🔥 OAUTH2 CONFIGURATION
                                .oauth2Login(oauth2 -> oauth2
                                                .loginPage("/showLoginPage")
                                                .successHandler(oAuth2LoginSuccessHandler))
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/showLoginPage")
                                                .permitAll());

                return http.build();
        }
}