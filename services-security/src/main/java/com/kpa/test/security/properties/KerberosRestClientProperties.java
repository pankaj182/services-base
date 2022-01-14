package com.kpa.test.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "kerberos.client")
public class KerberosRestClientProperties {
  private String principal;
  private String keytabPath;
  private Integer tokenTtl;

  public Integer getTokenTtl() {
    return (tokenTtl == null || tokenTtl == 0) ? 3600 : tokenTtl;
  }
}
