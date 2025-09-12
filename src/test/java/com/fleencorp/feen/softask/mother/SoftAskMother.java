package com.fleencorp.feen.softask.mother;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.model.MemberData;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;

import static com.fleencorp.feen.softask.util.SoftAskFeatureTestConstant.*;

public class SoftAskMother {

  public static AddSoftAskDto createAddSoftAskDto() {
    AddSoftAskDto dto = new AddSoftAskDto();
    dto.setTitle(SoftAskDtoDefaultTestConstants.TITLE);
    dto.setDescription(SoftAskDtoDefaultTestConstants.DESCRIPTION);
    dto.setOtherText(SoftAskDtoDefaultTestConstants.OTHER_TEXT);
    dto.setTags(SoftAskDtoDefaultTestConstants.TAGS);

    return dto;
  }

  public static SoftAsk createSoftAsk() {
    SoftAsk softAsk = new SoftAsk();
    softAsk.setSoftAskId(SoftAskDefaultTestConstants.ID_1);
    softAsk.setAuthorId(IsAMemberTestConstants.ID_1);

    return softAsk;
  }

  public static SoftAskResponse createSoftAskResponse() {
    SoftAskResponse softAskResponse = new SoftAskResponse();
    softAskResponse.setId(SoftAskResponseTestConstants.ID_1);

    return softAskResponse;
  }

  public static IsAMember createIsAMember() {
    MemberData memberData = new MemberData();
    memberData.setMemberId(IsAMemberTestConstants.ID_1);

    return memberData;
  }

  public static RegisteredUser createRegisteredUser() {
    RegisteredUser user = new RegisteredUser();
    user.setId(RegisteredUserDefaultTestConstants.ID_1);

    return user;
  }
}
