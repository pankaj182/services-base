package com.kpa.test.security.configs;

import com.kpa.test.common.enums.OS;
import com.kpa.test.security.adapters.Authenticator;
import com.kpa.test.security.properties.KerberosRestClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.kerberos.client.KerberosRestTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Lazy
@Configuration
@EnableConfigurationProperties(KerberosRestClientProperties.class)
public class KerberosClientRestConfig {
  private final KerberosRestClientProperties kerberosRestClientProperties;
  private final OS targetOS;

  private static final Logger LOGGER = LoggerFactory.getLogger(KerberosClientRestConfig.class);

  @Autowired
  public KerberosClientRestConfig(KerberosRestClientProperties kerberosRestClientProperties, OS targetOS) throws IOException, InterruptedException {
    this.kerberosRestClientProperties = kerberosRestClientProperties;
    this.targetOS = targetOS;
  }

  @Scheduled(fixedRateString = "${auth.cache-refresh-interval:21600000 }")
  public void refreshTicketCache() throws IOException, InterruptedException {
    if(StringUtils.isEmpty(kerberosRestClientProperties.getKeytabPath()) ||
        targetOS == OS.WINDOWS) {
      return;
    }
    LOGGER.info("Provided keytab path is {}", kerberosRestClientProperties.getKeytabPath());

    List<String> commands = new ArrayList<>();
    commands.add("bash");
    commands.add("-c");
    commands.add(String.format("/usr/kerberos/bin/kinit -k -t %1$s %2$s", kerberosRestClientProperties.getKeytabPath(), kerberosRestClientProperties.getPrincipal()));
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(commands);
    Process process = processBuilder.start();
    BufferedReader output = new BufferedReader(new java.io.InputStreamReader(process.getInputStream()));
    String line;
    while ((line = output.readLine()) != null) {
      LOGGER.debug("kinit: {}", line);
    }
    LOGGER.debug("Waiting for kinit to complete");
    int exitCode = process.waitFor();
    if(exitCode != 0) {
      LOGGER.error("kinit failed with exit code {}. Ticket cache refresh failed", exitCode);
    } else {
      LOGGER.info("kinit completed successfully. Ticket cache refreshed");
    }
  }

  @Bean
  public RestTemplate restTemplate() {
    return new KerberosRestTemplate("", kerberosRestClientProperties.getPrincipal());
  }

  @Bean
  @Lazy
  public Authenticator authenticator() {
    if(StringUtils.isEmpty(kerberosRestClientProperties.getKeytabPath()) ||
        targetOS == OS.WINDOWS) {
      return Authenticator.getCurrentUserAuthenticator("TDMSRDB SecureAccess 1.0");
    }
    return Authenticator.getKeytabAuthenticator("TDMSRDB SecureAccess 1.0", kerberosRestClientProperties.getPrincipal(),
        kerberosRestClientProperties.getKeytabPath(), kerberosRestClientProperties.getTokenTtl());
  }
}
