package com.fleencorp.feen.chat.space.model.info.core;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.chat.space.constant.core.ChatSpaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "status",
  "status_text",
  "status_text_2",
  "status_text_3",
  "status_text_4",
  "status_other_text"
})
public class ChatSpaceStatusInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("status")
  private ChatSpaceStatus status;

  @JsonProperty("status_text")
  private String statusText;

  @JsonProperty("status_text_2")
  private String statusText2;

  @JsonProperty("status_text_3")
  private String statusText3;

  @JsonProperty("status_text_4")
  private String statusText4;

  @JsonProperty("status_other_text")
  private String statusOtherText;

  public static ChatSpaceStatusInfo of(final ChatSpaceStatus status, final String statusText, final String statusText2, final String statusText3, final String statusText4, final String statusText5) {
    return new ChatSpaceStatusInfo(status, statusText, statusText2, statusText3, statusText4, statusText5);
  }

}
