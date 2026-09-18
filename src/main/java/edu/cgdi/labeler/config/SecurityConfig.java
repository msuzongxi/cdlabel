package edu.cgdi.labeler.config;

import edu.cgdi.labeler.mapper.UserMapper;
import edu.cgdi.labeler.model.UserAccount;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/webjars/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/api/admin/annotations/clear").hasRole("ADMIN")
                .requestMatchers("/exports", "/exports/**").hasRole("ADMIN")
                .requestMatchers("/assignments", "/api/admin/**", "/review", "/review/**", "/api/review/**")
                    .hasAnyRole("ADMIN", "SUPERVISOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .successHandler((request, response, authentication) -> {
                    boolean isLabeler = authentication.getAuthorities().stream().anyMatch(a ->
                        a.getAuthority().equals("ROLE_LABELER")
                    );
                    boolean isAdminOrSupervisor = authentication.getAuthorities().stream().anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_SUPERVISOR")
                    );

                    if (isLabeler) {
                        response.sendRedirect("/label/queue");
                    } else if (isAdminOrSupervisor) {
                        response.sendRedirect("/assignments");
                    } else {
                        response.sendRedirect("/");
                    }
                })
            )
            .logout(logout -> logout.logoutSuccessUrl("/login?logout"));

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(UserMapper userMapper) {
        return username -> {
            UserAccount account = userMapper.findByUsername(username);
            if (account == null) {
                throw new UsernameNotFoundException(username);
            }

            return User.withUsername(account.getUsername())
                .password(account.getPasswordHash())
                .roles(account.getRole())
                .disabled(!account.isActive())
                .build();
        };
    }
}
