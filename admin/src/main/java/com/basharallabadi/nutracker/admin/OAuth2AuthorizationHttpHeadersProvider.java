package com.basharallabadi.nutracker.admin;

import de.codecentric.boot.admin.server.domain.entities.Instance;
import de.codecentric.boot.admin.server.web.client.HttpHeadersProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

public class OAuth2AuthorizationHttpHeadersProvider implements HttpHeadersProvider {
        private OAuth2AuthorizedClientService clientService;
        public OAuth2AuthorizationHttpHeadersProvider(OAuth2AuthorizedClientService clientService) {
            this.clientService = clientService;
        }

        @Override
        public HttpHeaders getHeaders(Instance application) {
            OAuth2AuthenticationToken token = OAuth2AuthenticationToken.class.cast(SecurityContextHolder.getContext().getAuthentication());
            OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
                    token.getAuthorizedClientRegistrationId(),
                    token.getName());
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.AUTHORIZATION, String.format("%s %s", OAuth2AccessToken.TokenType.BEARER, client.getAccessToken().getTokenValue()));
            return headers;
        }
    }