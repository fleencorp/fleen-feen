package com.fleencorp.feen.model.dto.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToLowerCase;
import com.fleencorp.base.converter.common.ToSentenceCase;
import com.fleencorp.base.converter.common.ToTitleCase;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.ValidEnum;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChatSpaceDto {

  @NotBlank(message = "{chat.space.title.NotBlank}")
  @Size(min = 10, max = 500, message = "{chat.space.title.Size}")
  @ToTitleCase
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{chat.space.description.NotBlank}")
  @Size(max = 3000, message = "{chat.space.description.Size}")
  @ToSentenceCase
  @JsonProperty("description")
  private String description;

  @NotBlank(message = "{chat.space.guidelinesOrRules.NotBlank}")
  @Size(max = 3000, message = "{chat.space.guidelinesOrRules.Size}")
  @ToSentenceCase
  @JsonProperty("guidelines_or_rules")
  private String guidelinesOrRules;

  @NotBlank(message = "{tags.NotBlank}")
  @Size(min = 1, max = 300, message = "{tags.Size}")
  @ToLowerCase
  @JsonProperty("tags")
  protected String tags;

  @NotNull(message = "{chatSpace.visibility.NotNull}")
  @ValidEnum(enumClass = ChatSpaceVisibility.class, message = "{chatSpace.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty("visibility")
  private String visibility;

  public ChatSpace toChatSpace(final Member member) {
    final ChatSpace chatSpace = toChatSpace();
    chatSpace.setMember(member);
    return chatSpace;
  }

  public ChatSpace toChatSpace() {
    return ChatSpace.builder()
      .title(title)
      .description(description)
      .guidelinesOrRules(guidelinesOrRules)
      .tags(tags)
      .spaceVisibility(getActualVisibility())
      .isActive(true)
      .isDeleted(false)
      .totalMembers(0L)
      .build();
  }

  public ChatSpaceVisibility getActualVisibility() {
    return ChatSpaceVisibility.of(visibility);
  }
}
