package com.fleencorp.feen.model.response.link;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.ApiResponse;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({
  "message"
})
public class DeleteLinkResponse extends ApiResponse {

  @Override
  public String getMessageCode() {
    return "delete.link";
  }

  public static DeleteLinkResponse of() {
    return new DeleteLinkResponse();
  }
}
