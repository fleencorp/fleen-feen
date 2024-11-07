package com.fleencorp.feen.model.response.external.aws;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "signed_urls"
})
public class SignedUrlsResponse {

  @JsonProperty("signed_urls")
  private List<SignedUrl> signedUrls;

  public static SignedUrlsResponse of(List<SignedUrl> signedUrls) {
    return new SignedUrlsResponse(signedUrls);
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonPropertyOrder({
    "url",
    "file_name",
    "generated_file_name",
    "content_type"
  })
  public static class SignedUrl {

    @JsonProperty("url")
    private String url;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("generated_file_name")
    private String generatedFileName;

    @JsonProperty("content_type")
    private String contentType;

    public static SignedUrl of(String url, String fileName, String generatedFileName, String contentType) {
      return new SignedUrl(url, fileName, generatedFileName, contentType);
    }
  }
}
