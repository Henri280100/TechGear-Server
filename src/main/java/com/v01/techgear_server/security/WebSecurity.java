package com.v01.techgear_server.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationEntryPoint;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@Slf4j
public class WebSecurity {
        @Autowired
        private JwtAuthenticationFilter jwtAuthenticationFilter;

        @Autowired
        JWTtoUserConvertor jwTtoUserConvertor;

        @Autowired
        KeyUtils keyUtils;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        UserDetailsManager userDetailsManager;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((authorize) -> authorize
                                                .requestMatchers("/api/v01/auth/**").permitAll()
                                                .requestMatchers("/api/v01/admin/auth/**").permitAll()
                                                .anyRequest().authenticated())
                                .csrf(csrf -> csrf.disable())
                                .cors(cors -> cors.disable())
                                .httpBasic(basic -> basic.disable())
                                .oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt.jwtAuthenticationConverter(
                                                (Converter<Jwt, ? extends AbstractAuthenticationToken>) jwTtoUserConvertor)))
                                .sessionManagement((session) -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling((exceptions) -> exceptions
                                                .authenticationEntryPoint(new BearerTokenAuthenticationEntryPoint())
                                                .accessDeniedHandler(new BearerTokenAccessDeniedHandler()));

                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
        }

        @Bean
        @Primary
        JwtDecoder jwtAccessTokenDecoder() {
                return NimbusJwtDecoder.withPublicKey(keyUtils.getAccessTokenPublicKey()).build();
        }

        @Bean
        @Primary
        JwtEncoder jwtAccessTokenEncoder() {
                JWK jwk = new RSAKey.Builder(keyUtils.getAccessTokenPublicKey())
                                .privateKey(keyUtils.getAccessTokenPrivateKey()).build();

                JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwks);
        }

        @Bean
        @Qualifier("jwtRefreshTokenDecoder")
        JwtDecoder jwtRefreshTokenDecoder() {
                return NimbusJwtDecoder.withPublicKey(keyUtils.getRefreshTokenPublicKey()).build();
        }

        @Bean
        @Qualifier("jwtRefreshTokenEncoder")
        JwtEncoder jwtRefreshTokenEncoder() {
                JWK jwk = new RSAKey.Builder(keyUtils.getRefreshTokenPublicKey())
                                .privateKey(keyUtils.getRefreshTokenPrivateKey()).build();

                JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
                return new NimbusJwtEncoder(jwks);
        }

        @Bean
        @Qualifier("jwtRefreshTokenAuthProvider")
        JwtAuthenticationProvider jwtRefreshTokenAuthProvider() {
                JwtAuthenticationProvider provider = new JwtAuthenticationProvider(jwtRefreshTokenDecoder());
                provider.setJwtAuthenticationConverter(jwTtoUserConvertor);
                return provider;
        }

        @Bean
        DaoAuthenticationProvider daoAuthenticationProvider() {
                DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
                provider.setUserDetailsService(userDetailsManager);
                provider.setPasswordEncoder(passwordEncoder);
                return provider;
        }
}
