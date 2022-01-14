package com.kpa.test.security.adapters;

import com.kpa.test.security.exceptions.AuthNException;
import com.kpa.test.security.ports.AuthProvider;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
@Lazy
public class AuthProviderAdapter implements AuthProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(AuthProviderAdapter.class);

  private final RestTemplate restTemplate;
  private final Authenticator authenticator;

  @Value("${auth.provider.url}")
  private String ssoUrl;


  @Autowired
  public AuthProviderAdapter(RestTemplate restTemplate, Authenticator authenticator) {
    this.restTemplate = restTemplate;
    this.authenticator = authenticator;
  }

  @Override
  public String getCookie() {

    String token = authenticator.getToken();
    return "SSO" + token;
  }

  @Override
  public String getSAMLCookie(String identityProviderUrl, String samlConsumerUrl) {
    HttpHeaders cookieJar = new HttpHeaders();
    cookieJar.add(HttpHeaders.COOKIE, getSSOCookieForSAMLAuth());
    try{
      HttpEntity<String> identityProviderResponse = restTemplate.exchange(identityProviderUrl, HttpMethod.GET, new HttpEntity<>(cookieJar), String.class);
      identityProviderResponse.getHeaders().get(HttpHeaders.SET_COOKIE).stream().forEach(c -> cookieJar.add(HttpHeaders.COOKIE, c));
      Document document = Jsoup.parse(identityProviderResponse.getBody());
      Element inputElement = document.getElementsByTag("input").first();
      MultiValueMap<String, String> postData = new LinkedMultiValueMap<>();
      postData.put(inputElement.attr("name"), Collections.singletonList(inputElement.attr("value")));
      cookieJar.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
      HttpEntity<String> samlPostResponse = restTemplate.exchange(samlConsumerUrl, HttpMethod.POST, new HttpEntity<>(postData, cookieJar), String.class);
      samlPostResponse.getHeaders().get(HttpHeaders.SET_COOKIE).stream().forEach(c -> cookieJar.add(HttpHeaders.COOKIE, c));
      cookieJar.clearContentHeaders();
      cookieJar.get(HttpHeaders.COOKIE).removeIf(s -> !s.startsWith("SAML"));
      return cookieJar.getFirst(HttpHeaders.COOKIE);
    } catch (Exception e) {
      LOGGER.error("SAML authentication failed", e);
      throw new AuthNException("SAML authentication failed", e);
    }
  }

  @Override
  public String getSSOCookieForSAMLAuth() {
    try {
      HttpEntity<String> response = restTemplate.getForEntity(ssoUrl, String.class);
      HttpHeaders headers = response.getHeaders();
      if(headers.getFirst("AUTH_STATUS").equals("OK")) {
        LOGGER.info("SSO authentication successful");
      }
      return headers.get(HttpHeaders.SET_COOKIE).stream().filter(c -> c.startsWith("SSO")).collect(Collectors.toList()).get(0);
    } catch (Exception e) {
      LOGGER.error("SSO authentication failed", e);
      throw new AuthNException("SSO authentication failed", e);
    }
  }
}
