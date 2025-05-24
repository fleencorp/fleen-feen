package com.fleencorp.feen.model.response.other;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.localizer.model.response.LocalizedResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "message",
  "total"
})
public class CountAllResponse extends LocalizedResponse {

  @JsonProperty("total")
  private long total;

  @Override
  public String getMessageCode() {
    return "count.all";
  }

  public static CountAllResponse of(final long total) {
    return new CountAllResponse(total);
  }
}
