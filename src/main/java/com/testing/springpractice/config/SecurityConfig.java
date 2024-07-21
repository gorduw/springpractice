package com.testing.springpractice.config;

import com.testing.springpractice.service.AdvisorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final AdvisorService advisorService;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService;

    public SecurityConfig(AdvisorService advisorService, PasswordEncoder passwordEncoder, OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService) {
        this.advisorService = advisorService;
        this.passwordEncoder = passwordEncoder;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        try {
            http
                    .authorizeHttpRequests(authz -> authz
                            .requestMatchers(HttpMethod.GET).hasAnyRole("USER", "ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.POST).hasAnyRole("ADMIN", "MANAGER")
                            .requestMatchers(HttpMethod.PUT).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.PATCH).hasRole("ADMIN")
                            .requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                            .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth2Login -> oauth2Login
                            .loginPage("/login")
                            .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                    .userService(customOAuth2UserService)
                            )
                    )
                    .formLogin(formLogin -> formLogin
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .usernameParameter("email")
                            .failureUrl("/login?error")
                            .permitAll()
                            .defaultSuccessUrl("/", true)
                    )
                    .authenticationProvider(authenticationProvider())
                    .httpBasic(Customizer.withDefaults())
                    .logout(logout -> logout.permitAll())
                    .csrf(csrf -> csrf.disable());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }

        try {
            return http.build();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
        }
        return null;
    }

    private Collection<? extends GrantedAuthority> grantAdminAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(advisorService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}