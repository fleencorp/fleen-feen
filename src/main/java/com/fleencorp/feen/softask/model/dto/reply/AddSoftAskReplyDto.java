package com.fleencorp.feen.softask.model.dto.reply;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.base.validator.IsNumber;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskAnswer;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
public class AddSoftAskReplyDto {

  @NotBlank(message = "{softAskReply.content.NotBlank}")
  @Size(min = 10, max = 4000, message = "{softAskReply.content.Size}")
  @JsonProperty("reply")
  private String content;

  @NotNull(message = "{softAskReply.parent.NotNull}")
  @JsonProperty("parent")
  private SoftAskAnswerParentDto parent;

  private boolean hasParent() {
    return nonNull(parent);
  }

  public Long getSoftAskAnswerId() {
    return hasParent() ? parent.getParentId() : null;
  }

  public SoftAskReply toSoftAskReply(final Member author, final SoftAsk softAsk, final SoftAskAnswer answer) {
    final SoftAskReply reply = new SoftAskReply();
    reply.setContent(content);
    reply.setAuthor(author);
    reply.setUserOtherName(author.getUsername());
    reply.setSoftAskId(softAsk.getSoftAskId());
    reply.setSoftAsk(softAsk);
    reply.setSoftAnswerId(answer.getSoftAskAnswerId());
    reply.setSoftAnswer(answer);

    return reply;
  }

  @Valid
  @Getter
  @Setter
  @NoArgsConstructor
  public static class SoftAskAnswerParentDto {

    @NotNull(message = "{softAskReply.parentId.NotNull}")
    @IsNumber(message = "{softAskReply.parentId.IsNumber}")
    @JsonProperty(value = "parent_id")
    private String parentId;

    public Long getParentId() {
      return nonNull(parentId) ? Long.parseLong(parentId) : null;
    }
  }
}

