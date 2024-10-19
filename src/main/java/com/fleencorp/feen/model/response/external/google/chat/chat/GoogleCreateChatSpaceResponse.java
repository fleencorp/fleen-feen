package com.fleencorp.feen.model.response.external.google.chat.chat;

import com.fleencorp.feen.model.response.external.google.chat.chat.base.GoogleChatSpaceResponse;
import lombok.*;

import static com.fleencorp.feen.util.external.google.GoogleApiUtil.getSpaceIdOrNameFrom;
import static java.util.Objects.nonNull;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GoogleCreateChatSpaceResponse {

  private String name;
  private String displayName;
  private String spaceUri;
  private GoogleChatSpaceResponse chatSpace;

  public static GoogleCreateChatSpaceResponse of(final String spaceIdOrName, final GoogleChatSpaceResponse response) {
    return GoogleCreateChatSpaceResponse.builder()
      .name(getSpaceIdOrNameFrom(spaceIdOrName))
      .displayName(nonNull(response) ? response.getDisplayName() : null)
      .spaceUri(nonNull(response) ? response.getSpaceUri() : null)
      .chatSpace(response)
      .build();
  }
}
