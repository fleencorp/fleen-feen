package com.fleencorp.feen.util;

import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;

public class SoftAskDtoRecordUtil {

  public static AddSoftAskDto createSoftAskDto() {
    AddSoftAskDto dto = new AddSoftAskDto();
    dto.setTitle("What's your favorite color?");
    dto.setDescription("About a person favorite color");
    dto.setOtherText("Favorite color");
    dto.setTags("color,person");
    dto.setLink("https://www.example.com");
    dto.setVisibility("PUBLIC");
    dto.setStatus("ANONYMOUS");

    return dto;
  }

  public static SoftAskParticipantDetail createSoftAskParticipantDetail() {
    SoftAskParticipantDetail detail = new SoftAskParticipantDetail();
    detail.setId(1L);
    detail.setUsername("GoldbergShamus1");
    detail.setDisplayName("Goldberg Shamus");
    detail.setAvatarUrl("https://avatars.example.com/1");
    detail.setSoftAskId(1L);

    return detail;
  }

  public static SoftAskResponse createSoftAskResponse() {
    SoftAskResponse response = new SoftAskResponse();
    response.setId(1L);
    response.setTitle("What's your favorite color?");

    return response;
  }
}
