package com.fleencorp.feen.softask.service.softask;

import com.fleencorp.feen.common.exception.FailedOperationException;
import com.fleencorp.feen.shared.chat.space.contract.IsAChatSpace;
import com.fleencorp.feen.shared.member.MemberNotFoundException;
import com.fleencorp.feen.shared.member.contract.IsAMember;
import com.fleencorp.feen.shared.poll.contract.IsAPoll;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.shared.stream.contract.IsAStream;
import com.fleencorp.feen.softask.constant.core.SoftAskParentType;
import com.fleencorp.feen.softask.exception.core.SoftAskParentNotFoundException;
import com.fleencorp.feen.softask.mapper.SoftAskMapper;
import com.fleencorp.feen.softask.model.domain.SoftAsk;
import com.fleencorp.feen.softask.model.domain.SoftAskParticipantDetail;
import com.fleencorp.feen.softask.model.dto.softask.AddSoftAskDto;
import com.fleencorp.feen.softask.model.factory.SoftAskFactory;
import com.fleencorp.feen.softask.model.response.softask.SoftAskAddResponse;
import com.fleencorp.feen.softask.model.response.softask.core.SoftAskResponse;
import com.fleencorp.feen.softask.mother.SoftAskMother;
import com.fleencorp.feen.softask.service.common.SoftAskCommonService;
import com.fleencorp.feen.softask.service.common.SoftAskOperationService;
import com.fleencorp.feen.softask.service.impl.softask.SoftAskServiceImpl;
import com.fleencorp.feen.softask.service.other.SoftAskQueryService;
import com.fleencorp.localizer.service.Localizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static com.fleencorp.feen.softask.util.SoftAskFeatureTestConstant.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SoftAskAddSoftAskTest {

  SoftAskCommonService softAskCommonService;
  SoftAskSearchService softAskSearchService;
  SoftAskOperationService softAskOperationService;
  SoftAskQueryService softAskQueryService;
  SoftAskMapper softAskMapper;
  Localizer localizer;

  SoftAskService softAskService;

  private SoftAsk softAsk;
  private AddSoftAskDto addSoftAskDto;
  private SoftAskResponse softAskResponse;

  private IsAMember member;
  private RegisteredUser user;


  @BeforeEach
  void setUp() {
    softAskCommonService = Mockito.mock(SoftAskCommonService.class);
    softAskSearchService = Mockito.mock(SoftAskSearchService.class);
    softAskOperationService = Mockito.mock(SoftAskOperationService.class);
    softAskQueryService = Mockito.mock(SoftAskQueryService.class);
    softAskMapper = Mockito.mock(SoftAskMapper.class);
    localizer = Mockito.mock(Localizer.class);

    softAskService = new SoftAskServiceImpl(
      softAskCommonService,
      softAskOperationService,
      softAskSearchService,
      softAskQueryService,
      softAskMapper,
      localizer
    );

    softAsk = SoftAskMother.createSoftAsk();
    addSoftAskDto = SoftAskMother.createAddSoftAskDto();
    softAskResponse = SoftAskMother.createSoftAskResponse();

    member = Mockito.mock(IsAMember.class);
    user = SoftAskMother.createRegisteredUser();
  }

  @Test
  @DisplayName("Test add soft ask successfully")
  void test_add_soft_ask_successfully() {
    // parameters
    SoftAsk softAskMock = SoftAskMother.createSoftAsk();
    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    SoftAskAddResponse expectedResponse = SoftAskAddResponse.of(SoftAskResponseTestConstants.ID_1, softAskResponse);

    // when
    when(member.getMemberId()).thenReturn(IsAMemberTestConstants.ID_1);
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);

    doNothing().when(softAskOperationService).setGeoHashAndGeoPrefix(any(SoftAsk.class));
    when(softAskOperationService.save(any(SoftAsk.class))).thenReturn(softAskMock);

    when(softAskOperationService.generateParticipantDetail(anyLong(), anyLong())).thenReturn(participantDetail);
    when(softAskMapper.toSoftAskResponse(any(SoftAsk.class), any(IsAMember.class))).thenReturn(softAskResponse);
    doNothing().when(softAskCommonService).processSoftAskResponses(anyCollection(), any(IsAMember.class), any());

    when(localizer.of(any(SoftAskAddResponse.class))).thenReturn(expectedResponse);

    // act
    SoftAskAddResponse response = softAskService.addSoftAsk(addSoftAskDto, user);

    // assert
    assertNotNull(response);
    assertEquals(SoftAskDefaultTestConstants.ID_1, response.getSoftAskId());
    assertEquals(softAskResponse, response.getSoftAskResponse());

    // verify
    verify(softAskQueryService).findMemberOrThrow(user.getId());
    verify(softAskOperationService).save(any(SoftAsk.class));
    verify(softAskOperationService).generateParticipantDetail(anyLong(), eq(user.getId()));
    verify(softAskMapper).toSoftAskResponse(any(SoftAsk.class), eq(member));
    verify(softAskCommonService).processSoftAskResponses(anyCollection(), eq(member), any());
    verify(localizer).of(any(SoftAskAddResponse.class));
  }

  @Test
  @DisplayName("Test add soft ask with chat space parent successfully")
  void test_add_soft_ask_chat_space_parent_successfully() {
    // parameters
    final Long parentId = Long.parseLong(SoftAskDtoDefaultTestConstants.PARENT_ID);
    AddSoftAskDto.SoftAskParentDto parent = new AddSoftAskDto.SoftAskParentDto();
    parent.setParentId(SoftAskDtoDefaultTestConstants.PARENT_ID);
    parent.setParentType(SoftAskDtoDefaultTestConstants.PARENT_TYPE_CHAT_SPACE);
    addSoftAskDto.setParent(parent);

    IsAChatSpace chatSpace = mock(IsAChatSpace.class);
    SoftAsk softAskMock = SoftAskMother.createSoftAsk();
    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    SoftAskAddResponse expectedResponse = SoftAskAddResponse.of(SoftAskResponseTestConstants.ID_1, softAskResponse);

    // when
    when(member.getMemberId()).thenReturn(IsAMemberTestConstants.ID_1);
    when(softAskQueryService.findChatSpaceOrThrow(parentId)).thenReturn(chatSpace);
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);

    doNothing().when(softAskOperationService).setGeoHashAndGeoPrefix(any(SoftAsk.class));
    when(softAskOperationService.save(any(SoftAsk.class))).thenReturn(softAskMock);

    when(softAskOperationService.generateParticipantDetail(anyLong(), anyLong())).thenReturn(participantDetail);
    when(softAskMapper.toSoftAskResponse(any(SoftAsk.class), any(IsAMember.class))).thenReturn(softAskResponse);
    doNothing().when(softAskCommonService).processSoftAskResponses(anyCollection(), any(IsAMember.class), any());

    when(localizer.of(any(SoftAskAddResponse.class))).thenReturn(expectedResponse);

    // act
    SoftAskAddResponse response = softAskService.addSoftAsk(addSoftAskDto, user);

    // assert
    assertNotNull(response);

    // verify
    verify(softAskQueryService).findChatSpaceOrThrow(eq(parentId));
  }


  @Test
  @DisplayName("Test add soft ask with poll parent successfully")
  void test_add_soft_ask_poll_parent_successfully() {
    // parameters
    final Long parentId = Long.parseLong(SoftAskDtoDefaultTestConstants.PARENT_ID);
    AddSoftAskDto.SoftAskParentDto parent = new AddSoftAskDto.SoftAskParentDto();
    parent.setParentId(SoftAskDtoDefaultTestConstants.PARENT_ID);
    parent.setParentType(SoftAskDtoDefaultTestConstants.PARENT_TYPE_POLL);
    addSoftAskDto.setParent(parent);

    IsAPoll poll = mock(IsAPoll.class);
    SoftAsk softAskMock = SoftAskMother.createSoftAsk();
    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    SoftAskAddResponse expectedResponse = SoftAskAddResponse.of(SoftAskResponseTestConstants.ID_1, softAskResponse);

    // when
    when(member.getMemberId()).thenReturn(IsAMemberTestConstants.ID_1);
    when(softAskQueryService.findPollOrThrow(parentId)).thenReturn(poll);
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);

    doNothing().when(softAskOperationService).setGeoHashAndGeoPrefix(any(SoftAsk.class));
    when(softAskOperationService.save(any(SoftAsk.class))).thenReturn(softAskMock);

    when(softAskOperationService.generateParticipantDetail(anyLong(), anyLong())).thenReturn(participantDetail);
    when(softAskMapper.toSoftAskResponse(any(SoftAsk.class), any(IsAMember.class))).thenReturn(softAskResponse);
    doNothing().when(softAskCommonService).processSoftAskResponses(anyCollection(), any(IsAMember.class), any());

    when(localizer.of(any(SoftAskAddResponse.class))).thenReturn(expectedResponse);

    // act
    SoftAskAddResponse response = softAskService.addSoftAsk(addSoftAskDto, user);

    // assert
    assertNotNull(response);

    // verify
    verify(softAskQueryService).findPollOrThrow(eq(parentId));
  }

  @Test
  @DisplayName("Test add soft ask with stream parent successfully")
  void test_add_soft_ask_stream_parent_successfully() {
    // parameters
    final Long parentId = Long.parseLong(SoftAskDtoDefaultTestConstants.PARENT_ID);
    AddSoftAskDto.SoftAskParentDto parent = new AddSoftAskDto.SoftAskParentDto();
    parent.setParentId(SoftAskDtoDefaultTestConstants.PARENT_ID);
    parent.setParentType(SoftAskDtoDefaultTestConstants.PARENT_TYPE_STREAM);
    addSoftAskDto.setParent(parent);

    IsAStream stream = mock(IsAStream.class);
    SoftAsk softAskMock = SoftAskMother.createSoftAsk();
    SoftAskParticipantDetail participantDetail = new SoftAskParticipantDetail();
    SoftAskAddResponse expectedResponse = SoftAskAddResponse.of(SoftAskResponseTestConstants.ID_1, softAskResponse);

    // when
    when(member.getMemberId()).thenReturn(IsAMemberTestConstants.ID_1);
    when(softAskQueryService.findStreamOrThrow(parentId)).thenReturn(stream);
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);

    doNothing().when(softAskOperationService).setGeoHashAndGeoPrefix(any(SoftAsk.class));
    when(softAskOperationService.save(any(SoftAsk.class))).thenReturn(softAskMock);

    when(softAskOperationService.generateParticipantDetail(anyLong(), anyLong())).thenReturn(participantDetail);
    when(softAskMapper.toSoftAskResponse(any(SoftAsk.class), any(IsAMember.class))).thenReturn(softAskResponse);
    doNothing().when(softAskCommonService).processSoftAskResponses(anyCollection(), any(IsAMember.class), any());

    when(localizer.of(any(SoftAskAddResponse.class))).thenReturn(expectedResponse);

    // act
    SoftAskAddResponse response = softAskService.addSoftAsk(addSoftAskDto, user);

    // assert
    assertNotNull(response);

    // verify
    verify(softAskQueryService).findStreamOrThrow(eq(parentId));
  }

  @Test
  @DisplayName("Test member not found")
  void test_add_soft_ask_member_not_found() {
    // parameters
    when(softAskQueryService.findMemberOrThrow(user.getId()))
      .thenThrow(new MemberNotFoundException());

    // when
    // act
    assertThrows(MemberNotFoundException.class, () -> softAskService.addSoftAsk(addSoftAskDto, user));

    // verify
    verify(softAskQueryService).findMemberOrThrow(user.getId());
    verifyNoInteractions(softAskOperationService, softAskMapper, localizer);
  }

  @Test
  @DisplayName("Test add soft ask with no parent")
  void test_add_soft_ask_no_parent() {
    // parameters
    AddSoftAskDto.SoftAskParentDto parent = SoftAskMother.createSoftAskParentDtoEmpty();
    SoftAsk softAskMock = SoftAskMother.createSoftAsk();
    SoftAskAddResponse expectedResponse = SoftAskAddResponse.of(SoftAskResponseTestConstants.ID_1, softAskResponse);

    addSoftAskDto.setParent(parent);

    // when
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);
    when(softAskOperationService.save(any(SoftAsk.class))).thenReturn(softAskMock);
    when(softAskOperationService.generateParticipantDetail(anyLong(), anyLong())).thenReturn(new SoftAskParticipantDetail());
    when(softAskMapper.toSoftAskResponse(any(SoftAsk.class), any(IsAMember.class))).thenReturn(softAskResponse);
    when(localizer.of(any(SoftAskAddResponse.class))).thenReturn(expectedResponse);

    // act
    SoftAskAddResponse result = softAskService.addSoftAsk(addSoftAskDto, user);

    // assert
    assertNotNull(result);
    assertEquals(SoftAskDefaultTestConstants.ID_1, result.getSoftAskId());
    assertEquals(softAskResponse, result.getSoftAskResponse());

    // verify
    verify(softAskQueryService).findMemberOrThrow(user.getId());
  }

  @Test
  @DisplayName("Test add soft ask, chat space parent is not found")
  void test_add_soft_ask_chat_space_parent_not_found() {
    // parameters
    final Long parentId = Long.parseLong(SoftAskDtoDefaultTestConstants.PARENT_ID);

    // setters
    AddSoftAskDto.SoftAskParentDto parent = new AddSoftAskDto.SoftAskParentDto();
    parent.setParentId(SoftAskDtoDefaultTestConstants.PARENT_ID);
    parent.setParentType(SoftAskDtoDefaultTestConstants.PARENT_TYPE_CHAT_SPACE);
    addSoftAskDto.setParent(parent);

    // when
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);
    when(softAskQueryService.findChatSpaceOrThrow(parentId))
      .thenThrow(new SoftAskParentNotFoundException(SoftAskParentType.CHAT_SPACE));

    // assert
    assertThrows(SoftAskParentNotFoundException.class, () -> softAskService.addSoftAsk(addSoftAskDto, user));

    // verify
    verify(softAskQueryService).findChatSpaceOrThrow(eq(parentId));
  }

  @Test
  @DisplayName("Test add soft ask, poll parent is not found")
  void test_add_soft_ask_poll_parent_not_found() {
    // parameters
    final Long parentId = Long.parseLong(SoftAskDtoDefaultTestConstants.PARENT_ID);

    // setters
    AddSoftAskDto.SoftAskParentDto parent = new AddSoftAskDto.SoftAskParentDto();
    parent.setParentId(SoftAskDtoDefaultTestConstants.PARENT_ID);
    parent.setParentType(SoftAskDtoDefaultTestConstants.PARENT_TYPE_POLL);
    addSoftAskDto.setParent(parent);

    // when
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);
    when(softAskQueryService.findPollOrThrow(parentId))
      .thenThrow(new SoftAskParentNotFoundException(SoftAskParentType.POLL));

    // assert
    assertThrows(SoftAskParentNotFoundException.class, () -> softAskService.addSoftAsk(addSoftAskDto, user));

    // verify
    verify(softAskQueryService).findPollOrThrow(eq(parentId));
  }

  @Test
  @DisplayName("Test add soft ask, stream parent is not found")
  void test_add_soft_ask_stream_parent_not_found() {
    // parameters
    final Long parentId = Long.parseLong(SoftAskDtoDefaultTestConstants.PARENT_ID);

    // setters
    AddSoftAskDto.SoftAskParentDto parent = new AddSoftAskDto.SoftAskParentDto();
    parent.setParentId(SoftAskDtoDefaultTestConstants.PARENT_ID);
    parent.setParentType(SoftAskDtoDefaultTestConstants.PARENT_TYPE_STREAM);
    addSoftAskDto.setParent(parent);

    // when
    when(softAskQueryService.findMemberOrThrow(user.getId())).thenReturn(member);
    when(softAskQueryService.findStreamOrThrow(parentId))
      .thenThrow(new SoftAskParentNotFoundException(SoftAskParentType.STREAM));

    // assert
    assertThrows(SoftAskParentNotFoundException.class, () -> softAskService.addSoftAsk(addSoftAskDto, user));

    // verify
    verify(softAskQueryService).findStreamOrThrow(eq(parentId));
  }

  @Test
  @DisplayName("Test add soft ask, throw failed operation exception")
  void test_add_soft_ask_throw_failed_operation_exception() {
    assertThrows(FailedOperationException.class,
      () -> SoftAskFactory.toSoftAsk(null, SoftAskDtoDefaultTestConstants.NULL_TITLE, member));
  }
}