package com.algaworks.algamoneyapi.config;

import com.algaworks.algamoneyapi.config.property.AlgamoneyApiProperty;
import com.algaworks.algamoneyapi.model.ApplicationUser;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenCustomizer;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;

import java.io.InputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Configuration
@Profile("oauth-security")
public class AuthorizationServerConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AlgamoneyApiProperty algamoneyApiProperty;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient angularClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("angular")
                .clientSecret(passwordEncoder.encode("angular"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUris(uris -> uris.addAll(algamoneyApiProperty.getSecurity().getAllowedRedirects()))
                .scope("read")
                .scope("write")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(24))
                        .build())
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(true)
                                .build())
                .build();
        RegisteredClient mobileClient = RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("mobile")
                .clientSecret(passwordEncoder.encode("mobile"))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUris(uris -> uris.addAll(algamoneyApiProperty.getSecurity().getAllowedRedirects()))
                .scope("read")
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofMinutes(30))
                        .refreshTokenTimeToLive(Duration.ofDays(24))
                        .build())
                .clientSettings(
                        ClientSettings.builder()
                                .requireAuthorizationConsent(false)
                                .build())
                .build();
        return new InMemoryRegisteredClientRepository(
                Arrays.asList(
                        angularClient,
                        mobileClient
                )
        );
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authFilterChain(HttpSecurity http) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        return http.formLogin(Customizer.withDefaults()).build();
    }

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> jwtBuildCustomizer() {
        return (oAuth2TokenContext) -> {
            UsernamePasswordAuthenticationToken authenticationToken = oAuth2TokenContext.getPrincipal();
            ApplicationUser applicationUser = (ApplicationUser) authenticationToken.getPrincipal();

            Set<String> authorities = new HashSet<>();
            for (GrantedAuthority authority : applicationUser.getGrantedAuthorities()) {
                authorities.add(authority.getAuthority());
            }

            oAuth2TokenContext.getClaims().claim("name", applicationUser.getUserName());
            oAuth2TokenContext.getClaims().claim("authorities", authorities);
        };
    }

    @Bean
    public JWKSet jwkSet() throws Exception {
        final InputStream inputStream = new ClassPathResource("keystore/algamoney.jks").getInputStream();

        final KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(inputStream, "123456".toCharArray());

        RSAKey rsaKey = RSAKey.load(
                keyStore,
                "algamoney",
                "123456".toCharArray()
        );

        return new JWKSet(rsaKey);
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(JWKSet jwkSet) {
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }
    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer(algamoneyApiProperty.getSecurity().getAuthServerUrl())
                .build();
    }
}
