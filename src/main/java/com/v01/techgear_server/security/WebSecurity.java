package com.v01.techgear_server.security;

import static org.springframework.security.config.Customizer.withDefaults;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
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

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import com.v01.techgear_server.service.CustomOAuth2UserService;
import com.v01.techgear_server.service.CustomOidcUserService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableWebSecurity
public class WebSecurity {

        @Autowired
        private OAuth2LoginSuccessHandler OAuth2LoginSuccessHandler;

        @Autowired
        private OAuth2LoginFailureHandler OAuth2LoginFailureHandler;

        @Autowired
        UserDetailsService userDetailsService;

        @Autowired
        JWTtoUserConvertor jwTtoUserConvertor;

        @Autowired
        KeyUtils keyUtils;

        @Autowired
        PasswordEncoder passwordEncoder;

        @Autowired
        UserDetailsManager userDetailsManager;

        private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurity.class);

        @SuppressWarnings("removal")
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests((authorize) -> {
                                        try {
                                                authorize.requestMatchers("/api/v01/upload/**").permitAll()
                                                                .requestMatchers("/api/v01/auth/register",
                                                                                "/api/v01/auth/login",
                                                                                "/api/v01/auth/logout")
                                                                .permitAll()
                                                                .requestMatchers("/api/v01/auth/verify-email",
                                                                                "/api/v01/auth/resend-verification-email")
                                                                .permitAll()
                                                                .requestMatchers("/api/v01/auth/**")
                                                                .hasAnyAuthority("USER", "ADMIN")
                                                                .requestMatchers("/api/v01/admin/auth/**")
                                                                .hasAuthority("ADMIN")
                                                                .requestMatchers("/api/v01/admin/**")
                                                                .hasAuthority("ADMIN")
                                                                .anyRequest().authenticated().and()
                                                                .formLogin(login -> login
                                                                                .loginPage("/api/v01/auth/login")
                                                                                .defaultSuccessUrl("/", true)
                                                                                .permitAll())
                                                                .logout(logout -> logout
                                                                                .logoutUrl("/api/v01/auth/logout") // URL
                                                                                                                   // to
                                                                                                                   // trigger
                                                                                // logout
                                                                                .logoutSuccessHandler((request,
                                                                                                response,
                                                                                                authentication) -> {
                                                                                        response.setStatus(
                                                                                                        HttpServletResponse.SC_OK); // Set
                                                                                                                                    // success
                                                                                                                                    // status
                                                                                                                                    // code
                                                                                        response.getWriter().write(
                                                                                                        "Successfully logged out");
                                                                                })
                                                                                .invalidateHttpSession(true) // Invalidate
                                                                                                             // the
                                                                                                             // session
                                                                                .deleteCookies("JSESSIONID",
                                                                                                "remember-me") // Delete
                                                                                                               // cookies
                                                                                                               // on
                                                                                                               // logout
                                                )
                                                                .rememberMe(rememberMe -> rememberMe
                                                                                .key("uniqueAndSecret")
                                                                                .tokenValiditySeconds(86400)
                                                                                .rememberMeCookieName("remember-me"))

                                                                .exceptionHandling(handling -> handling
                                                                                .accessDeniedPage("/403"));
                                        } catch (Exception e) {
                                                LOGGER.error("Something went wrong", e);

                                        }
                                })
                                .formLogin(withDefaults())
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

                // Add OAuth2 login for Facebook and Google
                http.oauth2Login(oauth2Login -> oauth2Login
                                .loginPage("/api/v01/auth/login")
                                .defaultSuccessUrl("/", true)
                                .userInfoEndpoint(userInfo -> userInfo
                                                .oidcUserService(oidcUserService()) // Handle Google OIDC login
                                                .userService(oAuth2UserService())) // Handle Facebook OAuth2 login
                                .successHandler(OAuth2LoginSuccessHandler)
                                .failureHandler(OAuth2LoginFailureHandler));

                return http.build();
        }

        // Custom OIDC user service for Google OAuth2 login
        @Bean
        public OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
                return new CustomOidcUserService(); // Custom implementation of OidcUserService
        }

        // Custom OAuth2 user service for Facebook OAuth2 login
        @Bean
        public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService() {
                return new CustomOAuth2UserService(); // Custom implementation of DefaultOAuth2UserService
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
                provider.setPasswordEncoder(passwordEncoder);
                provider.setUserDetailsService(userDetailsManager);
                return provider;
        }

}
