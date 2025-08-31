package com.fleencorp.feen.softask.model.dto.reply;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.common.SoftAskWithParentDto;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddSoftAskReplyDto extends SoftAskWithParentDto {

  @NotBlank(message = "{softAskReply.content.NotBlank}")
  @Size(min = 10, max = 4000, message = "{softAskReply.content.Size}")
  @JsonProperty("reply")
  private String content;

  public SoftAskReply toSoftAskReply(final Member author, final SoftAsk softAsk, final SoftAskReply parentReply) {
    final SoftAskReply reply = new SoftAskReply();
    reply.setContent(content);
    reply.setVisible(true);

    reply.setAuthorId(author.getMemberId());
    reply.setAuthor(author);

    reply.setSoftAskId(softAsk.getSoftAskId());
    reply.setSoftAsk(softAsk);

    reply.setLatitude(BigDecimal.valueOf(latitude));
    reply.setLongitude(BigDecimal.valueOf(longitude));

    reply.setModerationStatus(ModerationStatus.CLEAN);
    reply.setLocationVisibility(LocationVisibility.GLOBAL);
    reply.setMoodTag(getMood());

    if (parentReply != null) {
      reply.setParentReplyId(parentReply.getSoftAskReplyId());
      reply.setParentReply(parentReply);
    }

    return reply;
  }
}

