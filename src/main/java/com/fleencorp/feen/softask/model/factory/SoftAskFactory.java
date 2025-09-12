package com.fleencorp.feen.softask.model.factory;

import com.fleencorp.feen.common.constant.location.LocationVisibility;
import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.constant.core.SoftAskStatus;
import com.fleencorp.feen.softask.constant.core.SoftAskVisibility;
import com.fleencorp.feen.softask.constant.other.ModerationStatus;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.util.SoftAskUtil;

import java.math.BigDecimal;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public final class SoftAskFactory {

  private SoftAskFactory() {}

  public static SoftAsk toSoftAsk(
      final AddSoftAskDto dto,
      final String parentTitle,
      final IsAMember author) {

    checkParameters(dto, author);

    final SoftAskParentType parentType = dto.getParentType();
    final SoftAsk softAsk = new SoftAsk();

    setBaseFields(dto, parentTitle, author, softAsk);
    setParentDetails(dto, parentType, softAsk);
    setLocationDetails(dto, softAsk);

    return softAsk;
  }

  private static void checkParameters(AddSoftAskDto dto, IsAMember author) {
    if (isNull(dto) || isNull(author)) {
      throw FailedOperationException.of();
    }
  }

  private static void setLocationDetails(AddSoftAskDto dto, SoftAsk softAsk) {
    if (dto.hasLatitudeAndLongitude()) {
      softAsk.setLatitude(BigDecimal.valueOf(dto.getLatitude()));
      softAsk.setLongitude(BigDecimal.valueOf(dto.getLongitude()));
    }
  }

  private static void setParentDetails(AddSoftAskDto dto, SoftAskParentType parentType, SoftAsk softAsk) {
    if (nonNull(parentType)) {
      switch (parentType) {
        case CHAT_SPACE -> {
          softAsk.setSoftAskParentType(SoftAskParentType.CHAT_SPACE);
          softAsk.setChatSpaceId(dto.getParentId());
        }
        case POLL -> {
          softAsk.setSoftAskParentType(SoftAskParentType.POLL);
          softAsk.setPollId(dto.getParentId());
        }
        case STREAM -> {
          softAsk.setSoftAskParentType(SoftAskParentType.STREAM);
          softAsk.setStreamId(dto.getParentId());
        }
      }
    }
  }

  private static void setBaseFields(AddSoftAskDto dto, String parentTitle, IsAMember author, SoftAsk softAsk) {
    final String title = SoftAskUtil.getSoftAskTitle(dto.getQuestion());
    softAsk.setTitle(title);

    softAsk.setDescription(dto.getQuestion());
    softAsk.setMoodTag(dto.getMood());
    softAsk.setVisible(true);

    softAsk.setAuthorId(author.getMemberId());
    softAsk.setParentId(dto.getParentId());
    softAsk.setParentTitle(parentTitle);

    softAsk.setTags(null);
    softAsk.setLink(null);

    softAsk.setModerationStatus(ModerationStatus.CLEAN);
    softAsk.setLocationVisibility(LocationVisibility.GLOBAL);

    softAsk.setSoftAskStatus(SoftAskStatus.ANONYMOUS);
    softAsk.setSoftAskVisibility(SoftAskVisibility.PUBLIC);
  }
}

