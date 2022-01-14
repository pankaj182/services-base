package com.kpa.test.security.ports;

public interface AuthProvider {

  String getCookie();

  String getSAMLCookie(String identityProviderUrl, String samlConsumerUrl);

  String getSSOCookieForSAMLAuth();
}
