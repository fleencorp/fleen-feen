package com.fleencorp.feen.softask.mother;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.member.model.MemberData;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteParentType;
import com.fleencorp.feen.softask.constant.core.vote.SoftAskVoteType;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.domain.SoftAskVote;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.dto.vote.SoftAskVoteDto;
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

  public static AddSoftAskDto.SoftAskParentDto createSoftAskParentDtoEmpty() {
    return new AddSoftAskDto.SoftAskParentDto();
  }

  public static SoftAskVoteDto.SoftAskVoteParentDto createSoftAskVoteParentDto() {
    SoftAskVoteDto.SoftAskVoteParentDto dto = new SoftAskVoteDto.SoftAskVoteParentDto();
    dto.setSoftAskId(SoftAskDefaultTestConstants.ID_1_Str);
    dto.setVoteParentType(SoftAskVoteDtoDefaultTestConstants.PARENT_TYPE_SOFT_ASK);

    return dto;
  }

  public static SoftAskVoteDto.SoftAskVoteParentDto createSoftAskVoteNoParentTypeDto() {
    return new SoftAskVoteDto.SoftAskVoteParentDto();
  }

  public static SoftAskVoteDto.SoftAskVoteParentDto createSoftAskReplyVoteParentDto() {
    SoftAskVoteDto.SoftAskVoteParentDto dto = new SoftAskVoteDto.SoftAskVoteParentDto();
    dto.setVoteParentType(SoftAskVoteDtoDefaultTestConstants.PARENT_TYPE_SOFT_ASK_REPLY);
    dto.setSoftAskId(SoftAskDefaultTestConstants.ID_1_Str);
    dto.setSoftAskReplyId(SoftAskReplyDefaultTestConstants.ID_1_Str);

    return dto;
  }

  public static SoftAskVoteDto createSoftAskVoteDtoVoted() {
    SoftAskVoteDto softAskVoteDto = new SoftAskVoteDto();
    softAskVoteDto.setVoteType(SoftAskVoteDtoDefaultTestConstants.VOTED);
    softAskVoteDto.setParent(createSoftAskVoteParentDto());

    return softAskVoteDto;
  }

  public static SoftAskVoteDto createSoftAskVoteDtoNotVoted() {
    SoftAskVoteDto softAskVoteDto = new SoftAskVoteDto();
    softAskVoteDto.setVoteType(SoftAskVoteDtoDefaultTestConstants.NOT_VOTED);
    softAskVoteDto.setParent(createSoftAskVoteParentDto());

    return softAskVoteDto;
  }

  public static SoftAskVoteDto createSoftAskReplyVoteDtoVoted() {
    SoftAskVoteDto softAskVoteDto = new SoftAskVoteDto();
    softAskVoteDto.setVoteType(SoftAskVoteDtoDefaultTestConstants.VOTED);
    softAskVoteDto.setParent(createSoftAskReplyVoteParentDto());

    return softAskVoteDto;
  }

  public static SoftAskVoteDto createSoftAskReplyVoteDtoNotVoted() {
    SoftAskVoteDto softAskVoteDto = new SoftAskVoteDto();
    softAskVoteDto.setVoteType(SoftAskVoteDtoDefaultTestConstants.NOT_VOTED);
    softAskVoteDto.setParent(createSoftAskReplyVoteParentDto());

    return softAskVoteDto;
  }

  public static SoftAskVoteDto createSoftAskVoteDtoNoParentType() {
    SoftAskVoteDto softAskVoteDto = new SoftAskVoteDto();
    softAskVoteDto.setVoteType(SoftAskVoteDtoDefaultTestConstants.VOTED);
    softAskVoteDto.setParent(createSoftAskVoteNoParentTypeDto());

    return softAskVoteDto;
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

  public static SoftAskVote createSoftAskVoteForVoted() {
    SoftAskVote softAskVote = new SoftAskVote();
    softAskVote.setVoteId(SoftAskVoteDefaultTestConstants.ID_1);
    softAskVote.setMemberId(IsAMemberTestConstants.ID_1);
    softAskVote.setSoftAskId(SoftAskDefaultTestConstants.ID_1);

    SoftAskVoteParentType parentType = SoftAskVoteParentType.of(SoftAskVoteDtoDefaultTestConstants.PARENT_TYPE_SOFT_ASK);
    SoftAskVoteType voteType = SoftAskVoteType.of(SoftAskVoteDtoDefaultTestConstants.VOTED);
    softAskVote.setParentType(parentType);
    softAskVote.setVoteType(voteType);

    return softAskVote;
  }

  public static SoftAskVote createSoftAskVoteForNotVoted() {
    SoftAskVote softAskVote = new SoftAskVote();
    softAskVote.setVoteId(SoftAskVoteDefaultTestConstants.ID_1);
    softAskVote.setMemberId(IsAMemberTestConstants.ID_1);
    softAskVote.setSoftAskId(SoftAskDefaultTestConstants.ID_1);

    SoftAskVoteParentType parentType = SoftAskVoteParentType.of(SoftAskVoteDtoDefaultTestConstants.PARENT_TYPE_SOFT_ASK);
    SoftAskVoteType voteType = SoftAskVoteType.of(SoftAskVoteDtoDefaultTestConstants.NOT_VOTED);
    softAskVote.setParentType(parentType);
    softAskVote.setVoteType(voteType);

    return softAskVote;
  }

  public static SoftAskVote createSoftAskReplyVoteForVoted() {
    SoftAskVote softAskVote = new SoftAskVote();
    softAskVote.setVoteId(SoftAskVoteDefaultTestConstants.ID_1);
    softAskVote.setMemberId(IsAMemberTestConstants.ID_1);
    softAskVote.setSoftAskId(SoftAskReplyDefaultTestConstants.ID_1);

    SoftAskVoteParentType parentType = SoftAskVoteParentType.of(SoftAskVoteDtoDefaultTestConstants.PARENT_TYPE_SOFT_ASK_REPLY);
    SoftAskVoteType voteType = SoftAskVoteType.of(SoftAskVoteDtoDefaultTestConstants.VOTED);
    softAskVote.setParentType(parentType);
    softAskVote.setVoteType(voteType);

    return softAskVote;
  }

  public static SoftAskVote createSoftAskReplyVoteForNotVoted() {
    SoftAskVote softAskVote = new SoftAskVote();
    softAskVote.setVoteId(SoftAskVoteDefaultTestConstants.ID_1);
    softAskVote.setMemberId(IsAMemberTestConstants.ID_1);
    softAskVote.setSoftAskId(SoftAskReplyDefaultTestConstants.ID_1);

    SoftAskVoteParentType parentType = SoftAskVoteParentType.of(SoftAskVoteDtoDefaultTestConstants.PARENT_TYPE_SOFT_ASK_REPLY);
    SoftAskVoteType voteType = SoftAskVoteType.of(SoftAskVoteDtoDefaultTestConstants.NOT_VOTED);
    softAskVote.setParentType(parentType);
    softAskVote.setVoteType(voteType);

    return softAskVote;
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
