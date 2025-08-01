package com.fleencorp.feen.softask.model.dto.softask;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.constant.core.SoftAskStatus;
import com.fleencorp.feen.softask.constant.core.SoftAskVisibility;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
public class AddSoftAskDto {

  @NotBlank(message = "{softAsk.title.NotBlank}")
  @Size(min = 1, max = 500, message = "{softAsk.title.Size}")
  @JsonProperty("title")
  private String title;

  @NotBlank(message = "{softAsk.description.NotBlank}")
  @Size(min = 1, max = 4000, message = "{softAsk.description.Size}")
  @JsonProperty("description")
  private String description;

  @NotBlank(message = "{softAsk.otherText.NotBlank}")
  @Size(min = 1, max = 4000, message = "{softAsk.otherText.Size}")
  @JsonProperty("other_text")
  private String otherText;

  @NotBlank(message = "{softAsk.tags.NotBlank}")
  @Size(min = 1, max = 1000, message = "{softAsk.tags.Size}")
  @JsonProperty(value = "tags", access = JsonProperty.Access.READ_ONLY)
  private String tags;

  @URL(message = "{softAsk.link.URL}")
  @Size(min = 1, max = 1000, message = "{softAsk.link.Size}")
  @JsonProperty(value = "link", access = JsonProperty.Access.READ_ONLY)
  private String link = null;

  @NotNull(message = "{softAsk.visibility.NotNull}")
  @OneOf(enumClass = SoftAskVisibility.class, message = "{softAsk.visibility.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty(value = "visibility", access = JsonProperty.Access.READ_ONLY)
  private String visibility = SoftAskVisibility.PUBLIC.name();

  @NotNull(message = "{softAsk.status.NotNull}")
  @OneOf(enumClass = SoftAskStatus.class, message = "{softAsk.status.Type}", ignoreCase = true)
  @ToUpperCase
  @JsonProperty(value = "status", access = JsonProperty.Access.READ_ONLY)
  private String status = SoftAskStatus.ANONYMOUS.name();

  @NotNull(message = "{softAsk.parent.NotNull}")
  @JsonProperty("parent")
  private SoftAskParentDto parent;

  public boolean hasParent() {
    return nonNull(parent) && nonNull(parent.getParentId()) && nonNull(parent.getParentType());
  }

  public boolean hasNoParent() {
    return isNull(parent);
  }

  public Long getParentId() {
    return nonNull(parent) ? parent.getParentId() : null;
  }

  public SoftAskParentType getParentType() {
    return hasParent() ? parent.getParentType() : null;
  }

  public boolean isChatSpaceParent() {
    return hasParent() && parent.isChatSpaceParent();
  }

  public boolean isStreamParent() {
    return hasParent() && parent.isStreamParent();
  }

  public SoftAsk toSoftAsk(final Member author, final String parentTitle, final SoftAskParentType parentType, final ChatSpace chatSpace, final FleenStream stream) {
    final SoftAskVisibility softAskVisibility = SoftAskVisibility.of(visibility);
    final SoftAskStatus softAskStatus = SoftAskStatus.of(status);

    final SoftAsk softAsk = new SoftAsk();
    softAsk.setTitle(title);
    softAsk.setDescription(description);
    softAsk.setOtherText(otherText);
    softAsk.setTags(tags);
    softAsk.setLink(link);
    softAsk.setSoftAskVisibility(softAskVisibility);
    softAsk.setSoftAskStatus(softAskStatus);
    softAsk.setAuthorId(author.getMemberId());
    softAsk.setAuthor(author);
    softAsk.setUserOtherName(author.getUsername());
    softAsk.setParentId(getParentId());
    softAsk.setParentTitle(parentTitle);
    softAsk.setSoftAskParentType(parentType);

    softAsk.setChatSpaceId(getParentId());
    softAsk.setChatSpace(chatSpace);

    softAsk.setStreamId(getParentId());
    softAsk.setStream(stream);

    return softAsk;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskParentDto {

    @IsNumber(message = "{softAsk.parentId.IsNumber}")
    @JsonProperty(value = "parent_id", access = JsonProperty.Access.READ_ONLY)
    private String parentId = null;

    @OneOf(enumClass = SoftAskParentType.class, message = "{softAsk.parentType.Type}", ignoreCase = true)
    @ToUpperCase
    @JsonProperty(value = "parent_type", access = JsonProperty.Access.READ_ONLY)
    private String parentType;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public SoftAskParentType getParentType() {
      return SoftAskParentType.of(parentType);
    }

    public boolean isStreamParent() {
      return SoftAskParentType.isStream(getParentType());
    }

    public boolean isChatSpaceParent() {
      return SoftAskParentType.isChatSpace(getParentType());
    }

  }
}
