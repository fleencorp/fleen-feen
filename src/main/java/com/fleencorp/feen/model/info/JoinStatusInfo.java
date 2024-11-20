package com.fleencorp.feen.model.info;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.stream.JoinStatus;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "join_status",
  "join_status_text",
  "join_status_text_2"
})
public class JoinStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("join_status")
  private JoinStatus joinStatus;

  @JsonProperty("join_status_text")
  private String joinStatusText;

  @JsonProperty("join_status_text_2")
  private String joinStatusText2;

  public static JoinStatusInfo of(final JoinStatus joinStatus, final String joinStatusText, final String joinStatusText2) {
    return JoinStatusInfo.builder()
      .joinStatus(joinStatus)
      .joinStatusText(joinStatusText)
      .joinStatusText2(joinStatusText2)
      .build();
  }

  public static JoinStatusInfo of() {
    return new JoinStatusInfo();
  }
}

