package com.fleencorp.feen.model.response.security;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.base.model.response.base.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message"
})
public class SignOutResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "sign.out";
  }

  public static SignOutResponse of() {
    return SignOutResponse.builder()
      .build();
  }
}
