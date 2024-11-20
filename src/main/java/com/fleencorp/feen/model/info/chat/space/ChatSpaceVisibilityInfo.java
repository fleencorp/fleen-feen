package com.fleencorp.feen.model.info.chat.space;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import lombok.*;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
  "visibility",
  "visibility_text"
})
public class ChatSpaceVisibilityInfo {

  @JsonFormat(shape = STRING)
  @JsonProperty("visibility")
  private ChatSpaceVisibility visibility;

  @JsonProperty("visibility_text")
  private String visibilityText;

  public static ChatSpaceVisibilityInfo of(final ChatSpaceVisibility visibility, final String visibilityText) {
    return ChatSpaceVisibilityInfo.builder()
      .visibility(visibility)
      .visibilityText(visibilityText)
      .build();
  }
}
