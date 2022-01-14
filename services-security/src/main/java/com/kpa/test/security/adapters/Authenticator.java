package com.kpa.test.security.adapters;

import com.kpa.test.common.enums.OS;
import com.kpa.test.security.exceptions.AuthNException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;
import java.io.IOException;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class Authenticator {
  public static final String LOGIN_MODULE_NAME = "com.sun.security.auth.module.Krb5LoginModule";
  public static final String DESKTOP_SSO_URL = "https://authn.web.xxx.com/desktopsso/login";
  public static final String DESKTOPSSO_LOGIN_REGEX = "^(http|https)://\\w+(-\\w+)?(-\\w+-\\w+)?.*.xxx.com(:\\d+)?/desktopsso/login$";
  static final int DEFAULT_TTL = 600;
  private static final Logger LOGGER = LoggerFactory.getLogger(Authenticator.class.getName());
  private static final String KRB5CCNAME = "KRB5CCNAME";
  private static final String SUBJECT_CREDS_ONLY = "javax.security.auth.useSubjectCredsOnly";
  private static final Map<Authenticator, AppConfigurationEntry[]> configurationMap = new HashMap<>();
  private static final ThreadLocal<Authenticator> currentAuthenticator = new ThreadLocal<>();
  private static final Map<String, Authenticator> existingAuthenticators = new HashMap<>();

  static {
    Configuration config = Configuration.getConfiguration();
    if (config != null) {
      LOGGER.info("Found previous configuration");
      if (config.getAppConfigurationEntry("com.sun.security.jgss.krb5.initiate") != null) {
        LOGGER.warn("com.sun.security.jgss.krb5.initiate is also handled by another configuration");
      }

      Configuration.setConfiguration(new Authenticator.AuthenticationConfiguration(config));
    } else {
      LOGGER.info("Did not find previous configuration");
      Configuration.setConfiguration(new Authenticator.AuthenticationConfiguration(null));
    }
  }

  protected String dssoURL;
  private final String agent;
  private String cachedToken;
  private long lastDSSSOAccessTime;
  private int cacheTTLInMillis;
  private long cacheTime;
  private final boolean isWindowsPlatform;
  private volatile boolean threadRunning;
  private final Object threadLockObject;
  private Thread backgroundThread;
  private final CredentialType credentialType;

  private Authenticator(String agent, CredentialType credentialType, String principal, String keytabPath, int ttlSeconds) throws AuthNException {
    this.threadLockObject = new Object();
    this.backgroundThread = null;
    this.dssoURL = DESKTOP_SSO_URL;
    OS osType = OS.getOS(System.getProperty("os.name"));
    this.isWindowsPlatform = osType == OS.WINDOWS;

    if (ttlSeconds < DEFAULT_TTL) {
      ttlSeconds = DEFAULT_TTL;
    }

    String host = "";

    try {
      host = java.net.InetAddress.getLocalHost().getCanonicalHostName();
    } catch (UnknownHostException unknownHostException) {
      throw new AuthNException("Unable to acquire local host name!");
    }

    this.agent = agent + "@" + host;
    this.credentialType = credentialType;
    this.cacheTTLInMillis = ttlSeconds * 1000;
    if (this.isWindowsPlatform) {
      System.setProperty(SUBJECT_CREDS_ONLY, "false");
    }

    Map<String, String> options = new HashMap<>();
    options.put("doNotPrompt", "true");
    if (this.credentialType == CredentialType.CurrentUser) {
      options.put("useTicketCache", "true");
      String ticketCacheName = System.getenv(KRB5CCNAME);
      if (ticketCacheName != null && !ticketCacheName.isEmpty()) {
        options.put("ticketCache", ticketCacheName);
      }
    } else {
      options.put("useKeyTab", "true");
      options.put("keyTab", keytabPath);
      options.put("principal", principal);
    }

    synchronized (configurationMap) {
      configurationMap.put(this, new AppConfigurationEntry[]{new AppConfigurationEntry(LOGIN_MODULE_NAME, AppConfigurationEntry.LoginModuleControlFlag.REQUIRED, options)});
    }
  }

  private Authenticator(String agent, CredentialType credentialType, int ttlSeconds) throws AuthNException {
    this(agent, credentialType, null, null, DEFAULT_TTL);
  }

  public static Authenticator getCurrentUserAuthenticator(String agent) throws AuthNException {
    return getCurrentUserAuthenticator(agent, DEFAULT_TTL);
  }

  public static Authenticator getCurrentUserAuthenticator(String agent, int ttlSeconds) throws AuthNException {
    Authenticator authenticator = null;
    synchronized (existingAuthenticators) {
      authenticator = existingAuthenticators.get("currentUser");
      if (authenticator == null) {
        authenticator = new Authenticator(agent, CredentialType.CurrentUser, ttlSeconds);
        existingAuthenticators.put("currentUser", authenticator);
      }
      return authenticator;
    }
  }

  public static Authenticator getKeytabAuthenticator(String agent, String principal, String keytabPath) throws AuthNException {
    return getKeytabAuthenticator(agent, principal, keytabPath, DEFAULT_TTL);
  }

  public static Authenticator getKeytabAuthenticator(String agent, String principal, String keytabPath, int ttlSeconds) throws AuthNException {
    validateInputs(agent, principal, keytabPath);
    Authenticator authenticator = null;
    synchronized (existingAuthenticators) {
      authenticator = existingAuthenticators.get(principal.toUpperCase());
      if (authenticator == null) {
        authenticator = new Authenticator(agent, CredentialType.Keytab, principal, keytabPath, ttlSeconds);
        existingAuthenticators.put(principal.toUpperCase(), authenticator);
      }
      return authenticator;
    }
  }

  private static void validateInputs(String agent, String principal, String keytab) throws AuthNException {
    if (agent == null || agent.isEmpty()) {
      throw new AuthNException("Agent is empty or null");
    }

    if (principal == null || principal.isEmpty()) {
      throw new AuthNException("Principal is empty or null");
    }

    if (keytab == null || keytab.isEmpty()) {
      throw new AuthNException("Keytab is empty or null");
    }
  }

  private void makeDSSOCallSync() throws AuthNException {
    try {
      currentAuthenticator.set(this);
      this.makeDSSSOCall();
    } catch (Exception e) {
      throw new AuthNException(e.getMessage(), e);
    } finally {
      currentAuthenticator.remove();
    }
  }

  private void makeDSSOCallAsync() {
    LOGGER.info("Starting background thread for DSSO call");
    this.threadRunning = true;
    Runnable runnable = new Runnable() {
      public void run() {
        boolean var = false;
        try {
          var = true;
          Authenticator.currentAuthenticator.set(Authenticator.this);
          Authenticator.this.makeDSSSOCall();
          var = false;
        } catch (Exception var2) {
          LOGGER.error("Error in background thread for DSSO call", var2);
          var = false;
        } finally {
          if (var) {
            Authenticator.this.threadRunning = false;
            Authenticator.currentAuthenticator.remove();
            synchronized (Authenticator.this.threadLockObject) {
              Authenticator.this.backgroundThread = null;
            }
          }
        }

        Authenticator.this.threadRunning = false;
        Authenticator.currentAuthenticator.remove();
        synchronized (Authenticator.this.threadLockObject) {
          Authenticator.this.backgroundThread = null;
          return;
        }
      }
    };
    synchronized (this.threadLockObject) {
      this.backgroundThread = new Thread(runnable);
    }
    this.backgroundThread.start();
  }

  private void makeDSSSOCall() throws IOException, AuthNException {
    long now = System.currentTimeMillis();
    if (now - this.lastDSSSOAccessTime < 10000L) {
      LOGGER.info("DSSSO call is being made too frequently. Skipping call.");
    } else {
      this.lastDSSSOAccessTime = now;
      URL authURL = new URL(this.dssoURL);
      HttpURLConnection con = null;
      synchronized (Configuration.getConfiguration()) {
        con = (HttpURLConnection) authURL.openConnection();
        con.setUseCaches(false);
        con.setRequestProperty("User-Agent", this.agent);
        con.getContent();
      }

      int responseCode = con.getResponseCode();
      LOGGER.info("Received response code: " + responseCode + " from the service");
      Map<String, List<String>> headers = con.getHeaderFields();
      con.disconnect();
      if (responseCode != 200) {
        throw new AuthNException("Received response code: " + responseCode + " from the service");
      } else {
        Iterator i$ = ((List) headers.get("Set-Cookie")).iterator();
        while (i$.hasNext()) {
          String cookie = (String) i$.next();
          Iterator i$1 = HttpCookie.parse(cookie).iterator();
          while (i$1.hasNext()) {
            HttpCookie cookie1 = (HttpCookie) i$1.next();
            if ("GSSSO".equals(cookie1.getName())) {
              synchronized (this) {
                LOGGER.info("Successfully retrieved SSO token from the service");
                this.cachedToken = cookie1.getValue();
                this.cacheTime = System.currentTimeMillis();
                return;
              }
            }
          }
        }
        LOGGER.error("Token not found in the response");
      }
    }
  }

  protected synchronized long getLastDSSSOAccessTime() {
    return this.lastDSSSOAccessTime;
  }

  protected synchronized void setLastDSSSOAccessTime(long lastDSSSOAccessTime) {
    this.lastDSSSOAccessTime = lastDSSSOAccessTime;
  }

  protected synchronized long getCacheTime() {
    return this.cacheTime;
  }

  protected synchronized void setCacheTime(long cacheTime) {
    this.cacheTime = cacheTime;
  }

  protected synchronized boolean isThreadRunning() {
    return this.threadRunning;
  }

  protected synchronized void setThreadRunning(boolean threadRunning) {
    this.threadRunning = threadRunning;
  }

  protected void waitForThread() {
    Thread t = null;
    synchronized (this.threadLockObject) {
      t = this.backgroundThread;
    }
    if (t != null) {
      try {
        t.join();
      } catch (InterruptedException e) {
      }
    }
  }

  protected synchronized void setCacheTTLInMillis(int ttlInMillis) {
    this.cacheTTLInMillis = ttlInMillis;
  }

  public void setDssoURL(String dssoURL) throws AuthNException {
    if (dssoURL == null || dssoURL.isEmpty()) {
      this.dssoURL = DESKTOP_SSO_URL;
    }
    if (dssoURL.matches(DESKTOPSSO_LOGIN_REGEX)) {
      this.dssoURL = dssoURL;
    } else {
      throw new AuthNException("DSSO URL is not valid!");
    }
  }

  public synchronized String getToken() {
    if(this.threadRunning) {
      return this.cachedToken;
    } else {
      long tokenAge = this.getCachedTokenAge();
      if(this.cachedToken == null ) {
        this.makeDSSOCallSync();
      } else if(tokenAge > this.cacheTTLInMillis / 2) {
        if(tokenAge > this.cacheTTLInMillis) {
          LOGGER.info("Token is expired. Making a new call to the service");
          this.makeDSSOCallSync();
        } else {
          this.makeDSSOCallAsync();
        }
      }

      if(cachedToken == null) {
        throw new AuthNException("Unable to retrieve token from the service");
      } else {
        return this.cachedToken;
      }
    }
  }

  private synchronized long getCachedTokenAge() {
    return this.cachedToken == null ? 0L : System.currentTimeMillis() - this.cacheTime;
  }


  private enum CredentialType {
    CurrentUser,
    Keytab;

    CredentialType() {
    }
  }

  public static class AuthenticationConfiguration extends Configuration {
    private Configuration baseConfiguration;

    public AuthenticationConfiguration(Configuration baseConfiguration) {
      this.baseConfiguration = baseConfiguration;
    }

    public Configuration getBaseConfiguration() {
      return this.baseConfiguration;
    }

    public void setBaseConfiguration(Configuration baseConfiguration) {
      this.baseConfiguration = baseConfiguration;
    }

    public AppConfigurationEntry[] getAppConfigurationEntry(String name) {
      Authenticator authenticator = currentAuthenticator.get();
      if (authenticator != null && "com.sun.security.jgss.krb5.initiate".equals(name)) {
        Authenticator.LOGGER.info("Getting COnfiguration Entry for " + name);
        synchronized (Authenticator.configurationMap) {
          return Authenticator.configurationMap.get(authenticator);
        }
      } else {
        return this.baseConfiguration != null ? this.baseConfiguration.getAppConfigurationEntry(name) : null;
      }
    }
  }
}
