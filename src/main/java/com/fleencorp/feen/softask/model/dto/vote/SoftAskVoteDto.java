package com.fleencorp.feen.softask.model.dto.vote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.converter.common.ToUpperCase;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoftAskVoteDto {

  @NotNull(message = "{softAskVote.parent.NotNull}")
  @JsonProperty("parent")
  private SoftAskVoteParentDto parent;

  @NotNull(message = "{softAskVote.type.NotNull}")
  @OneOf(enumClass = SoftAskVoteType.class, message = "{softAskVote.type.Type}")
  @ToUpperCase
  @JsonProperty("type")
  private String voteType;

  protected boolean hasParent() {
    return nonNull(parent);
  }

  public Long getSoftAskReplyId() {
    return hasParent() ? parent.getSoftAskReplyId() : null;
  }

  public Long getSoftAskId() {
    return hasParent() ? parent.getSoftAskId() : null;
  }

  public SoftAskVoteParentType getVoteParentType() {
    return hasParent() ? parent.getVoteParentType() : null;
  }

  public SoftAskVoteType getVoteType() {
    return SoftAskVoteType.of(voteType);
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskVoteParentDto {

    @NotNull(message = "{softAskVote.parentId.NotNull}")
    @IsNumber(message = "{softAskVote.parentId.IsNumber}")
    @JsonProperty(value = "soft_ask_id")
    private String softAskId;

    @IsNumber(message = "{softAskReply.parentId.IsNumber}")
    @JsonProperty(value = "soft_ask_reply_id")
    private String softAskReplyId;

    @NotNull(message = "{softAskVote.parentType.NotNull}")
    @OneOf(enumClass = SoftAskVoteParentType.class, message = "{softAskVote.parentType.Type}")
    @JsonProperty("parent_type")
    private String voteParentType;

    public Long getSoftAskReplyId() {
      return nonNull(softAskReplyId) ? Long.parseLong(softAskReplyId) : null;
    }

    public Long getSoftAskId() {
      return nonNull(softAskId) ? Long.parseLong(softAskId) : null;
    }

    public SoftAskVoteParentType getVoteParentType() {
      return SoftAskVoteParentType.of(voteParentType);
    }
  }
}
