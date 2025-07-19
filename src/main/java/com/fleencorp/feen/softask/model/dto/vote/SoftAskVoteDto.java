package com.fleencorp.feen.softask.model.dto.vote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.base.validator.OneOf;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.user.model.domain.Member;
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
  @JsonProperty("type")
  private String voteType;

  private boolean hasParent() {
    return nonNull(parent);
  }

  public Long getParentId() {
    return hasParent() ? parent.getParentId() : null;
  }

  public SoftAskVoteParentType getVoteParentType() {
    return hasParent() ? parent.getVoteParentType() : null;
  }

  public SoftAskVoteType getVoteType() {
    return SoftAskVoteType.of(voteType);
  }

  public SoftAskVote by(final Member member, final SoftAsk softAsk, final SoftAskAnswer softAskAnswer, final SoftAskReply softAskReply) {
    final SoftAskVoteParentType parentType = getVoteParentType();

    return switch (parentType) {
      case SOFT_ASK_ANSWER -> toSoftAskAnswerVote(softAskAnswer, member);
      case SOFT_ASK_REPLY -> toSoftAskReplyVote(softAskReply, member);
      case SOFT_ASK -> toSoftAskVote(softAsk, member);
    };
  }

  protected SoftAskVote toSoftAskAnswerVote(final SoftAskAnswer softAskAnswer, final Member member) {
    final SoftAskVote softAskVote = toVote(softAskAnswer.getSoftAskAnswerId(), member);
    softAskVote.setSoftAskAnswerId(softAskAnswer.getSoftAskAnswerId());
    softAskVote.setSoftAskAnswer(softAskAnswer);

    return softAskVote;
  }

  protected SoftAskVote toSoftAskReplyVote(final SoftAskReply softAskReply, final Member member) {
    final SoftAskVote softAskVote = toVote(softAskReply.getSoftAskReplyId(), member);
    softAskVote.setSoftAskReplyId(softAskReply.getSoftAskReplyId());
    softAskVote.setSoftAskReply(softAskReply);

    return softAskVote;
  }

  protected SoftAskVote toSoftAskVote(final SoftAsk softAsk, final Member member) {
    final SoftAskVote softAskVote = toVote(softAsk.getSoftAskId(), member);
    softAskVote.setSoftAskId(softAsk.getSoftAskId());
    softAskVote.setSoftAsk(softAsk);
    softAskVote.setParentTitle(softAsk.getTitle());

    return softAskVote;
  }


  protected SoftAskVote toVote(final Long parentId, final Member member) {
    final SoftAskVote softAskVote = new SoftAskVote();
    softAskVote.setParentId(parentId);
    softAskVote.setParentType(getVoteParentType());
    softAskVote.setVoteType(getVoteType());
    softAskVote.setMemberId(member.getMemberId());
    softAskVote.setMember(member);

    return softAskVote;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskVoteParentDto {

    @NotNull(message = "{softAskVote.parentId.NotNull}")
    @IsNumber(message = "{softAskVote.parentId.IsNumber}")
    @JsonProperty(value = "parent_id")
    private String parentId;

    @NotNull(message = "{softAskVote.parentType.NotNull}")
    @OneOf(enumClass = SoftAskVoteParentType.class, message = "{softAskVote.parentType.Type}")
    @JsonProperty("parent_type")
    private String voteParentType;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }

    public SoftAskVoteParentType getVoteParentType() {
      return SoftAskVoteParentType.of(voteParentType);
    }
  }
}
