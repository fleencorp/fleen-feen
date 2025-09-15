package com.fleencorp.feen.softask.service.reply;

import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.softask.constant.other.MoodTag;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.model.domain.SoftAskReply;
import com.fleencorp.feen.softask.model.dto.common.SoftAskWithParentDto;
import com.fleencorp.feen.softask.model.dto.reply.AddSoftAskReplyDto;
import com.fleencorp.feen.softask.model.response.reply.SoftAskReplyAddResponse;
import com.fleencorp.feen.softask.model.response.reply.core.SoftAskReplyResponse;
import com.fleencorp.feen.softask.mother.SoftAskMother;
import com.fleencorp.feen.softask.repository.reply.SoftAskReplyRepository;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.impl.reply.SoftAskReplyServiceImpl;
import com.fleencorp.feen.softask.service.other.SoftAskQueryService;
import com.fleencorp.feen.softask.service.softask.SoftAskSearchService;
import com.fleencorp.feen.softask.util.SoftAskFeatureTestConstant;
import com.fleencorp.localizer.service.Localizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SoftAskAddReplyTest {

  private SoftAskCommonService commonService;
  private SoftAskOperationService operationService;
  private SoftAskQueryService queryService;
  private SoftAskSearchService searchService;
  private SoftAskReplyRepository repository;
  private SoftAskMapper mapper;
  private Localizer localizer;

  private SoftAskReplyServiceImpl replyService;

  private AddSoftAskReplyDto dto;
  private RegisteredUser user;
  private IsAMember author;
  private SoftAsk softAsk;
  private SoftAskReply parentReply;
  private SoftAskReply reply;

  @BeforeEach
  void setUp() {
    commonService = Mockito.mock(SoftAskCommonService.class);
    operationService = Mockito.mock(SoftAskOperationService.class);
    queryService = Mockito.mock(SoftAskQueryService.class);
    searchService = Mockito.mock(SoftAskSearchService.class);
    repository = Mockito.mock(SoftAskReplyRepository.class);
    mapper = Mockito.mock(SoftAskMapper.class);
    localizer = Mockito.mock(Localizer.class);

    replyService = new SoftAskReplyServiceImpl(
      commonService,
      operationService,
      queryService,
      searchService,
      repository,
      mapper,
      localizer
    );

    dto = SoftAskMother.createAddSoftAskReplyDto();
    user = SoftAskMother.createRegisteredUser();
    author = SoftAskMother.createIsAMember();
    softAsk = SoftAskMother.createSoftAsk();
    parentReply = SoftAskMother.createSoftAskParentReply();
    reply = SoftAskMother.createSoftAskReply();
  }

  @Test
  @DisplayName("Should add reply to soft ask without parent")
  void should_add_reply_to_soft_ask_without_parent() {
    // parameters
    Long parentId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;

    SoftAskWithParentDto.SoftAskParentDto parentDto = new SoftAskWithParentDto.SoftAskParentDto();
    parentDto.setSoftAskId(SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1_Str);
    dto.setParent(parentDto);

    // when
    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    when(queryService.findMemberOrThrow(memberId)).thenReturn(author);
    when(searchService.findSoftAsk(parentId)).thenReturn(softAsk);
    when(repository.save(any())).thenReturn(reply);

    SoftAskReplyResponse replyResponse = new SoftAskReplyResponse();
    when(operationService.getOrAssignParticipantDetail(softAskId, memberId)).thenReturn(participantDetail);
    when(operationService.incrementSoftAskReplyCountAndGetReplyCount(softAskId)).thenReturn(1);
    when(mapper.toSoftAskReplyResponse(any(), any())).thenReturn(replyResponse);
    when(localizer.of(any(SoftAskReplyAddResponse.class))).thenAnswer(inv -> inv.getArgument(0));

    // act
    SoftAskReplyAddResponse result = replyService.addSoftAskReply(dto, user);

    // assert
    // verify
    assertThat(result).isNotNull();
    assertThat(result.getChildReplyCount()).isEqualTo(1);
    verify(repository).save(any(SoftAskReply.class));
    verify(operationService).incrementSoftAskReplyCountAndGetReplyCount(softAskId);
  }

  @Test
  void shouldAddReplyToParentReply() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;

    Long parentId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    String parentIdStr = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1_Str;
    String softAskReplyIdStr = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1_Str;

    SoftAskWithParentDto.SoftAskParentDto parentDto = new SoftAskWithParentDto.SoftAskParentDto();
    parentDto.setSoftAskId(parentIdStr);
    parentDto.setSoftAskReplyId(softAskReplyIdStr);
    dto.setParent(parentDto);

    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();

    // when
    when(queryService.findMemberOrThrow(memberId)).thenReturn(author);
    when(repository.findBySoftAskAndParentReply(softAskId, parentId)).thenReturn(Optional.of(parentReply));
    when(repository.save(any())).thenReturn(reply);
    when(operationService.getOrAssignParticipantDetail(softAskId, memberId)).thenReturn(participantDetail);
    when(operationService.incrementSoftAskReplyChildReplyCountAndGetReplyCount(softAskId, parentId)).thenReturn(2);

    SoftAskReplyResponse replyResponse = new SoftAskReplyResponse();
    when(mapper.toSoftAskReplyResponse(any(), any())).thenReturn(replyResponse);
    when(localizer.of(any(SoftAskReplyAddResponse.class))).thenAnswer(inv -> inv.getArgument(0));

    // act
    SoftAskReplyAddResponse result = replyService.addSoftAskReply(dto, user);

    // assert
    // verify
    assertThat(result).isNotNull();
    assertThat(result.getChildReplyCount()).isEqualTo(2);
    verify(repository).save(any(SoftAskReply.class));
    verify(operationService).incrementSoftAskReplyChildReplyCountAndGetReplyCount(softAskId, parentId);
  }

  @Test
  @DisplayName("Should add reply with location details")
  void should_add_reply_with_location_details() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    String softAskIdStr = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1_Str;

    Double latitude = SoftAskFeatureTestConstant.UserOtherDtoDefaultTestConstants.LATITUDE;
    Double longitude = SoftAskFeatureTestConstant.UserOtherDtoDefaultTestConstants.LONGITUDE;

    SoftAskWithParentDto.SoftAskParentDto parentDto = new SoftAskWithParentDto.SoftAskParentDto();
    parentDto.setSoftAskId(softAskIdStr);

    dto.setLatitude(latitude);
    dto.setLongitude(longitude);
    dto.setParent(parentDto);

    // when
    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    when(queryService.findMemberOrThrow(memberId)).thenReturn(author);
    when(searchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(repository.save(any())).thenAnswer(inv -> {
      SoftAskReply saved = inv.getArgument(0);
      assertThat(saved.getLatitude()).isEqualByComparingTo(latitude);
      assertThat(saved.getLongitude()).isEqualByComparingTo(longitude);

      return saved;
    });

    when(operationService.getOrAssignParticipantDetail(softAskId, memberId))
      .thenReturn(participantDetail);
    when(operationService.incrementSoftAskReplyCountAndGetReplyCount(softAskId)).thenReturn(3);
    when(mapper.toSoftAskReplyResponse(any(), any())).thenReturn(new SoftAskReplyResponse());
    when(localizer.of(any(SoftAskReplyAddResponse.class)))
      .thenAnswer(inv -> inv.getArgument(0));

    // when
    SoftAskReplyAddResponse result = replyService.addSoftAskReply(dto, user);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getChildReplyCount()).isEqualTo(3);
  }

  @Test
  @DisplayName("Should add reply without location details")
  void should_add_reply_without_location_details() {
    // parameters
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    String softAskIdStr = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1_Str;

    SoftAskWithParentDto.SoftAskParentDto parentDto = new SoftAskWithParentDto.SoftAskParentDto();
    parentDto.setSoftAskId(softAskIdStr);
    dto.setParent(parentDto);

    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    SoftAskReplyResponse replyResponse = new SoftAskReplyResponse();

    // when
    when(queryService.findMemberOrThrow(memberId)).thenReturn(author);
    when(searchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(repository.save(any())).thenAnswer(inv -> {
      SoftAskReply saved = inv.getArgument(0);

      assertThat(saved.getLatitude()).isNull();
      assertThat(saved.getLongitude()).isNull();
      return saved;
    });

    when(operationService.getOrAssignParticipantDetail(softAskId, memberId)).thenReturn(participantDetail);
    when(operationService.incrementSoftAskReplyCountAndGetReplyCount(softAskId)).thenReturn(4);
    when(mapper.toSoftAskReplyResponse(any(), any())).thenReturn(replyResponse);
    when(localizer.of(any(SoftAskReplyAddResponse.class))).thenAnswer(inv -> inv.getArgument(0));

    // act
    SoftAskReplyAddResponse result = replyService.addSoftAskReply(dto, user);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getChildReplyCount()).isEqualTo(4);
  }

  @Test
  @DisplayName("Should add reply with mood tag")
  void should_add_reply_with_mood_tag() {
    // parameters
    String mood = SoftAskFeatureTestConstant.UserOtherDtoDefaultTestConstants.MOOD;
    Long memberId = SoftAskFeatureTestConstant.IsAMemberTestConstants.ID_1;
    Long softAskId = SoftAskFeatureTestConstant.SoftAskDefaultTestConstants.ID_1;
    String softAskIdStr = SoftAskFeatureTestConstant.SoftAskReplyDefaultTestConstants.ID_1_Str;
    MoodTag moodTag = MoodTag.of(mood);

    SoftAskWithParentDto.SoftAskParentDto parentDto = new SoftAskWithParentDto.SoftAskParentDto();
    parentDto.setSoftAskId(softAskIdStr);

    dto.setMood(mood);
    dto.setParent(parentDto);

    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    SoftAskReplyResponse replyResponse = new SoftAskReplyResponse();

    // when
    when(queryService.findMemberOrThrow(memberId)).thenReturn(author);
    when(searchService.findSoftAsk(softAskId)).thenReturn(softAsk);
    when(repository.save(any())).thenAnswer(inv -> {
      SoftAskReply saved = inv.getArgument(0);

      assertThat(saved.getMoodTag()).isEqualTo(moodTag);
      return saved;
    });

    when(operationService.getOrAssignParticipantDetail(softAskId, memberId)).thenReturn(participantDetail);
    when(operationService.incrementSoftAskReplyCountAndGetReplyCount(softAskId)).thenReturn(5);
    when(mapper.toSoftAskReplyResponse(any(), any())).thenReturn(replyResponse);
    when(localizer.of(any(SoftAskReplyAddResponse.class))).thenAnswer(inv -> inv.getArgument(0));

    // act
    SoftAskReplyAddResponse result = replyService.addSoftAskReply(dto, user);

    // assert
    assertThat(result).isNotNull();
    assertThat(result.getChildReplyCount()).isEqualTo(5);
  }

}