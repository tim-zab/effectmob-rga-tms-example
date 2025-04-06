package rga.task.management.system.example.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import rga.task.management.system.example.security.JwtFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@Configuration
public class SecurityConfig {

    public static final String FIRST_SWAGGER_ENTRY_POINT = "/swagger-ui.html/**";
    public static final String SECOND_SWAGGER_ENTRY_POINT = "/swagger-ui/**";
    public static final String API_DOCS_ENTRY_POINT = "/v3/api-docs/**";
    public static final String SECURITY_ENTRY_POINT = "/rest/v1/security/**";

    private final JwtFilter filter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(managementConfigurer -> managementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(configurer -> configurer.configurationSource(request -> {
                    var configuration = new CorsConfiguration().applyPermitDefaultValues();
                    configuration.addAllowedMethod(HttpMethod.GET);
                    configuration.addAllowedMethod(HttpMethod.POST);
                    configuration.addAllowedMethod(HttpMethod.PUT);
                    configuration.addAllowedMethod(HttpMethod.DELETE);
                    return configuration;
                }))
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers(
                                        FIRST_SWAGGER_ENTRY_POINT,
                                        SECOND_SWAGGER_ENTRY_POINT,
                                        API_DOCS_ENTRY_POINT,
                                        SECURITY_ENTRY_POINT
                                ).permitAll()
                                .anyRequest().authenticated()
                )
                .addFilterAfter(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
