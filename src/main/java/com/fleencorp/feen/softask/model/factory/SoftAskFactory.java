package com.fleencorp.feen.softask.model.factory;

import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class SoftAskFactory {

  private SoftAskFactory() {}

  public static SoftAsk toSoftAsk(
      final AddSoftAskDto dto,
      final String parentTitle,
      final SoftAskParentType parentType,
      final IsAMember author) {

    checkParameters(dto, author);

    final SoftAsk softAsk = new SoftAsk();
    setBaseFields(dto, parentTitle, author, softAsk);
    setParentTitle(dto, parentType, softAsk);
    setLocationDetails(dto, softAsk);

    return softAsk;
  }

  private static void checkParameters(AddSoftAskDto dto, IsAMember author) {
    if (isNull(dto) || isNull(author)) {
      throw FailedOperationException.of();
    }
  }

  private static void setLocationDetails(AddSoftAskDto dto, SoftAsk softAsk) {
    if (nonNull(dto.getLatitude()) && nonNull(dto.getLongitude())) {
      softAsk.setLatitude(BigDecimal.valueOf(dto.getLatitude()));
      softAsk.setLongitude(BigDecimal.valueOf(dto.getLongitude()));
    }
  }

  private static void setParentTitle(AddSoftAskDto dto, SoftAskParentType parentType, SoftAsk softAsk) {
    if (nonNull(parentType)) {
      switch (parentType) {
        case CHAT_SPACE -> {
          softAsk.setSoftAskParentType(SoftAskParentType.CHAT_SPACE);
          softAsk.setChatSpaceId(dto.getParentId());
        }
        case STREAM -> {
          softAsk.setSoftAskParentType(SoftAskParentType.STREAM);
          softAsk.setStreamId(dto.getParentId());
        }
      }
    }
  }

  private static void setBaseFields(AddSoftAskDto dto, String parentTitle, IsAMember author, SoftAsk softAsk) {
    softAsk.setTitle(dto.getTitle());
    softAsk.setOtherText(dto.getOtherText());
    softAsk.setDescription(dto.getDescription());
    softAsk.setTags(dto.getTags());
    softAsk.setLink(dto.getLink());
    softAsk.setMoodTag(dto.getMood());
    softAsk.setVisible(true);
    softAsk.setAuthorId(author.getMemberId());

    softAsk.setParentId(dto.getParentId());
    softAsk.setParentTitle(parentTitle);

    softAsk.setModerationStatus(ModerationStatus.CLEAN);
    softAsk.setLocationVisibility(LocationVisibility.GLOBAL);

    softAsk.setSoftAskStatus(dto.getSoftAskStatus());
    softAsk.setSoftAskVisibility(dto.getSoftAskVisibility());
  }
}

