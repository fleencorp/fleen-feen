package com.fleencorp.feen.configuration.external.google.oauth2;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;

import static com.fleencorp.feen.constant.base.SimpleConstant.COMMA;
import static com.fleencorp.feen.oauth2.constant.Oauth2WebKey.*;

/**
* Configuration class for OAuth 2.0 credentials used by web clients.
*
* <p>This class represents OAuth 2.0 credentials configuration for web clients,
* providing properties such as projectId, clientId, clientSecret, authUri, tokenUri,
* javascriptOrigins, redirectUris, and authProviderX509CertUrl.</p>
*
* <p>These properties are typically configured through external properties files loaded
* using {@link ConfigurationProperties} annotation with the prefix "web"
* and {@link PropertySource} annotation pointing to "classpath:google-oauth2-web-client.properties".</p>
*
*
* @author Yusuf Alamu Musa
* @version 1.0
**/
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "web")
@PropertySources({
  @PropertySource("classpath:properties/google-oauth2-web-client.properties")
})
public class Oauth2Credential {

  private String projectId;
  private String clientId;
  private String clientSecret;
  private String authUri;
  private String tokenUri;
  private String javascriptOrigins;
  private String redirectUris;
  private String authProviderX509CertUrl;

  public List<String> getRedirectUriList() {
    return toList(redirectUris);
  }

  public List<String> getJavascriptOriginList() {
    return toList(javascriptOrigins);
  }

  public List<String> toList(final String value) {
    return List.of(value.split(COMMA));
  }

  public JsonFactory getJsonFactory() {
    return GsonFactory.getDefaultInstance();
  }

  /**
   * Converts the current object's properties to a GoogleClientSecrets object.
   *
   * <p>This method creates and populates a GoogleClientSecrets object using the current
   * object's properties such as clientId, clientSecret, authUri, tokenUri, redirect URIs,
   * and JavaScript origins.</p>
   *
   * <p>It sets these properties into corresponding fields of
   * GoogleClientSecrets.Details and sets additional metadata like javascriptOrigins.
   * The resulting GoogleClientSecrets object encapsulates all necessary configuration
   * for OAuth 2.0 authentication with Google services.</p>
   *
   * <p>The returned GoogleClientSecrets object can be used to configure and initialize
   * OAuth 2.0 flows within applications requiring Google API access.</p>
   *
   * @return A GoogleClientSecrets object initialized with the current object's properties.
   */
  public GoogleClientSecrets toGoogleClientSecrets() {
    final GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
    final GoogleClientSecrets.Details web = new GoogleClientSecrets.Details();
    web.setClientId(clientId);
    web.setAuthUri(authUri);
    web.setTokenUri(tokenUri);
    web.setClientSecret(clientSecret);
    web.setRedirectUris(getRedirectUriList());
    clientSecrets.setWeb(web);

    clientSecrets.set(X509_CERT_URL_KEY, authProviderX509CertUrl);
    clientSecrets.set(PROJECT_ID_KEY, projectId);
    clientSecrets.set(JAVASCRIPT_ORIGINS_KEY, getJavascriptOriginList());
    clientSecrets.setFactory(getJsonFactory());

    return clientSecrets;
  }

}
