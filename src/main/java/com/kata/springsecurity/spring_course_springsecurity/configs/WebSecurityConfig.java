package com.kata.springsecurity.spring_course_springsecurity.configs;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import com.kata.springsecurity.spring_course_springsecurity.service.MyCustomUserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationManagerFactories;
import org.springframework.security.authorization.AuthorizationManagerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.FactorGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final SuccessUserHandler successUserHandler;
    private final MyCustomUserDetailService userDetailService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(SuccessUserHandler successUserHandler,
                             MyCustomUserDetailService userDetailService,
                             PasswordEncoder passwordEncoder) {
        this.successUserHandler = successUserHandler;
        this.userDetailService = userDetailService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthorizationManagerFactory<RequestAuthorizationContext> mfa =
                AuthorizationManagerFactories
                        .<RequestAuthorizationContext>multiFactor()
                        .requireFactors(FactorGrantedAuthority.PASSWORD_AUTHORITY)
                        .build();

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").access(mfa.hasRole("ADMIN"))
                        .requestMatchers("/user/**").access(mfa.hasAnyRole("USER", "ADMIN"))
                        .requestMatchers("/login", "/", "/index", "/css/**", "/js/**", "/generate-hash").permitAll()
                        .anyRequest().access(mfa.authenticated())
                )
                .securityContext(context -> context.requireExplicitSave(false))
                .formLogin(form -> form
                        .usernameParameter("email")
                        .successHandler(successUserHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
