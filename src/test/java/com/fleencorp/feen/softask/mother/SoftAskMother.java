package com.fleencorp.feen.softask.mother;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.model.MemberData;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;

import static com.fleencorp.feen.softask.util.SoftAskFeatureTestConstant.*;

public class SoftAskMother {

  public static AddSoftAskDto createAddSoftAskDto() {
    AddSoftAskDto dto = new AddSoftAskDto();
    dto.setQuestion(SoftAskDtoDefaultTestConstants.QUESTION);

    return dto;
  }

  public static AddSoftAskReplyDto createAddSoftAskReplyDto() {
    AddSoftAskReplyDto dto = new AddSoftAskReplyDto();
    dto.setContent(SoftAskReplyDtoDefaultTestConstants.CONTENT);

    return dto;
  }

  public static SoftAsk createSoftAsk() {
    SoftAsk softAsk = new SoftAsk();
    softAsk.setSoftAskId(SoftAskDefaultTestConstants.ID_1);
    softAsk.setAuthorId(IsAMemberTestConstants.ID_1);

    return softAsk;
  }

  public static SoftAskReply createSoftAskReply() {
    SoftAskReply softAskReply = new SoftAskReply();
    softAskReply.setSoftAskReplyId(SoftAskReplyDefaultTestConstants.ID_2);
    softAskReply.setSoftAsk(createSoftAsk());
    softAskReply.setSoftAskId(SoftAskDefaultTestConstants.ID_1);
    softAskReply.setAuthorId(IsAMemberTestConstants.ID_1);

    return softAskReply;
  }

  public static SoftAskReply createSoftAskParentReply() {
    SoftAskReply softAskReply = new SoftAskReply();
    softAskReply.setSoftAsk(createSoftAsk());
    softAskReply.setSoftAskId(SoftAskReplyDefaultTestConstants.ID_1);
    softAskReply.setAuthorId(IsAMemberTestConstants.ID_1);

    return softAskReply;
  }


  public static SoftAskResponse createSoftAskResponse() {
    SoftAskResponse softAskResponse = new SoftAskResponse();
    softAskResponse.setId(SoftAskResponseTestConstants.ID_1);

    return softAskResponse;
  }

  public static IsAMember createIsAMember() {
    MemberData memberData = new MemberData();
    memberData.setMemberId(IsAMemberTestConstants.ID_1);
    memberData.setUsername(IsAMemberTestConstants.USERNAME);
    memberData.setPassword(IsAMemberTestConstants.PASSWORD);
    memberData.setEmailAddress(IsAMemberTestConstants.EMAIL_ADDRESS);

    return memberData;
  }

  public static RegisteredUser createRegisteredUser() {
    RegisteredUser user = new RegisteredUser();
    user.setId(RegisteredUserDefaultTestConstants.ID_1);

    return user;
  }
}
