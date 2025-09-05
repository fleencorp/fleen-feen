package com.fleencorp.feen.softask.model.factory;

import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class SoftAskReplyFactory {

  private SoftAskReplyFactory() {}

  public static SoftAskReply toSoftAskReply(
    final AddSoftAskReplyDto dto,
    final IsAMember author,
    final SoftAsk softAsk,
    final SoftAskReply parentReply) {

    checkParameters(dto, author, softAsk);

    final SoftAskReply reply = new SoftAskReply();
    setBaseFields(dto, author, softAsk, parentReply, reply);
    setLocationDetails(dto, reply);

    return reply;
  }

  private static void checkParameters(AddSoftAskReplyDto dto, IsAMember author, SoftAsk softAsk) {
    if (isNull(dto) || isNull(author) || isNull(softAsk)) {
      throw FailedOperationException.of();
    }
  }

  private static void setLocationDetails(AddSoftAskReplyDto dto, SoftAskReply reply) {
    if (nonNull(dto.getLatitude()) && nonNull(dto.getLongitude())) {
      reply.setLatitude(BigDecimal.valueOf(dto.getLatitude()));
      reply.setLongitude(BigDecimal.valueOf(dto.getLongitude()));
    }
  }

  private static void setBaseFields(
      AddSoftAskReplyDto dto,
      IsAMember author,
      SoftAsk softAsk,
      SoftAskReply parentReply,
      SoftAskReply reply) {

    reply.setContent(dto.getContent());
    reply.setVisible(true);
    reply.setAuthorId(author.getMemberId());

    if (nonNull(softAsk)) {
      reply.setSoftAskId(softAsk.getSoftAskId());
      reply.setSoftAsk(softAsk);
    }

    if (nonNull(parentReply)) {
      reply.setParentReplyId(parentReply.getSoftAskReplyId());
      reply.setParentReply(parentReply);
    }

    reply.setModerationStatus(ModerationStatus.CLEAN);
    reply.setLocationVisibility(LocationVisibility.GLOBAL);
    reply.setMoodTag(dto.getMood());
  }

  public static String truncateContent(String content) {
    if (content == null) return null;
    return content.length() <= 200 ? content : content.substring(0, 200);
  }

}

