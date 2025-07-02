package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.exception.chat.space.ChatSpaceNotFoundException;
import com.fleencorp.feen.exception.chat.space.core.NotAnAdminOfChatSpaceException;
import com.fleencorp.feen.exception.stream.StreamNotFoundException;
import com.fleencorp.feen.exception.stream.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.mapper.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.poll.model.dto.DeletePollDto;
import com.fleencorp.feen.poll.model.holder.AddPollParentDetailHolder;
import com.fleencorp.feen.poll.model.response.PollCreateResponse;
import com.fleencorp.feen.poll.model.response.PollDeleteResponse;
import com.fleencorp.feen.poll.model.response.base.PollResponse;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollService;
import com.fleencorp.feen.service.chat.space.ChatSpaceOperationsService;
import com.fleencorp.feen.service.chat.space.ChatSpaceService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PollServiceImpl implements PollService {

  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final ChatSpaceService chatSpaceService;
  private final MemberService memberService;
  private final StreamOperationsService streamOperationsService;
  private final PollCommonService pollCommonService;
  private final PollOperationsService pollOperationsService;
  private final PollMapper pollMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  public PollServiceImpl(
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final ChatSpaceService chatSpaceService,
      final MemberService memberService,
      final PollCommonService pollCommonService,
      final PollOperationsService pollOperationsService,
      final StreamOperationsService streamOperationsService,
      final PollMapper pollMapper,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.chatSpaceService = chatSpaceService;
    this.memberService = memberService;
    this.pollCommonService = pollCommonService;
    this.pollOperationsService = pollOperationsService;
    this.streamOperationsService = streamOperationsService;
    this.pollMapper = pollMapper;
    this.unifiedMapper = unifiedMapper;
    this.localizer = localizer;
  }

  /**
   * Creates a new poll based on the provided {@link AddPollDto} and the authenticated {@link RegisteredUser}.
   *
   * <p>This method first retrieves the {@link Member} entity associated with the user. It then determines and
   * validates the poll’s parent entity (if any) and constructs the {@link Poll} instance using the member and
   * parent information. The poll is persisted, and a localized {@link PollCreateResponse} is returned.</p>
   *
   * @param addPollDto the DTO containing data required to create the poll
   * @param user the authenticated user initiating the poll creation
   * @return a localized {@link PollCreateResponse} containing poll metadata and response
   * @throws MemberNotFoundException if the member associated with the user ID is not found
   * @throws ChatSpaceNotFoundException if the specified chat space does not exist
   * @throws NotAnAdminOfChatSpaceException if the member is not an admin or creator of the chat space
   * @throws StreamNotFoundException if the specified stream does not exist
   * @throws StreamNotCreatedByUserException if the member is not the organizer of the stream
   */
  @Override
  @Transactional
  public PollCreateResponse addPoll(final AddPollDto addPollDto, final RegisteredUser user)
    throws MemberNotFoundException, ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException,
      StreamNotFoundException, StreamNotCreatedByUserException {
    final Member member = memberService.findMember(user.getId());

    final AddPollParentDetailHolder parentDetailHolder = findAndValidateParent(addPollDto, member);
    final String parentTitle = parentDetailHolder.parentTitle();
    final ChatSpace chatSpace = parentDetailHolder.chatSpace();
    final FleenStream stream = parentDetailHolder.stream();

    final Poll poll = addPollDto.toPoll(member, parentTitle, chatSpace, stream);
    pollOperationsService.save(poll);

    final PollResponse response = pollMapper.toPollResponse(poll);
    final PollCreateResponse createResponse = PollCreateResponse.of(poll.getPollId(), response);

    return localizer.of(createResponse);
  }

  /**
   * Determines and validates the parent entity (stream or chat space) for a poll based on the given {@link AddPollDto}.
   *
   * <p>This method checks if a parent is specified in the DTO. If a parent exists, it validates the parent
   * according to its type—either a stream or a chat space. Validation ensures the given member has the appropriate
   * permission (organizer or admin). If no parent is specified or the type is unrecognized, an empty
   * {@link AddPollParentDetailHolder} is returned.</p>
   *
   * @param addPollDto the DTO containing poll creation data, including parent information
   * @param member the member attempting to create the poll
   * @return an {@link AddPollParentDetailHolder} representing the validated parent (stream or chat space), or empty if none
   * @throws MemberNotFoundException if the member is not found
   * @throws ChatSpaceNotFoundException if the specified chat space does not exist
   * @throws NotAnAdminOfChatSpaceException if the member is not an admin or creator of the chat space
   * @throws StreamNotFoundException if the specified stream does not exist
   * @throws StreamNotCreatedByUserException if the member is not the organizer of the stream
   */
  protected AddPollParentDetailHolder findAndValidateParent(final AddPollDto addPollDto, final Member member)
    throws MemberNotFoundException, ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException,
      StreamNotFoundException, StreamNotCreatedByUserException {

    if (addPollDto.hasNoParent()) {
      return AddPollParentDetailHolder.of();
    }

    if (addPollDto.isChatSpaceParent()) {
      return validateChatSpaceParent(addPollDto.getParentId(), member);
    }

    if (addPollDto.isStreamParent()) {
      return validateStreamParent(addPollDto.getParentId(), member);
    }

    return AddPollParentDetailHolder.of();
  }

  /**
   * Validates the specified {@code streamId} as the parent for a poll by ensuring the member is
   * the organizer of the stream.
   *
   * <p>This method retrieves the stream using the provided ID and checks if the given member is
   * the organizer of the stream. If validation succeeds, it returns an
   * {@link AddPollParentDetailHolder} with the stream as the parent.</p>
   *
   * @param streamId the ID of the stream to be validated
   * @param member the member requesting to add a poll under the stream
   * @return an {@link AddPollParentDetailHolder} representing the validated stream as the poll parent
   * @throws StreamNotFoundException if the stream with the given ID is not found
   * @throws StreamNotCreatedByUserException if the member is not the organizer of the stream
   */
  protected AddPollParentDetailHolder validateStreamParent(final Long streamId, final Member member)
      throws StreamNotFoundException, StreamNotCreatedByUserException {
    final FleenStream stream = streamOperationsService.findStream(streamId);
    stream.checkIsOrganizer(member.getMemberId());

    return AddPollParentDetailHolder.of(null, stream);
  }

  /**
   * Validates the specified {@code chatSpaceId} as the parent for a poll by ensuring the member is
   * either the creator or an admin of the chat space.
   *
   * <p>This method retrieves the chat space using the provided ID and verifies that the given member
   * has administrative rights or is the creator of the chat space. If validation passes, it returns
   * a {@link AddPollParentDetailHolder} containing the chat space as the parent.</p>
   *
   * @param chatSpaceId the ID of the chat space to be validated
   * @param member the member requesting to add a poll under the chat space
   * @return an {@link AddPollParentDetailHolder} representing the validated chat space as the poll parent
   * @throws ChatSpaceNotFoundException if the chat space with the given ID is not found
   * @throws NotAnAdminOfChatSpaceException if the member is neither an admin nor the creator of the chat space
   */
  protected AddPollParentDetailHolder validateChatSpaceParent(final Long chatSpaceId, final Member member)
      throws ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException {
    final ChatSpace chatSpace = chatSpaceOperationsService.findChatSpace(chatSpaceId);
    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);

    return AddPollParentDetailHolder.of(chatSpace, null);
  }

  /**
   * Deletes the specified {@link Poll} if the given {@link RegisteredUser} has the required permissions.
   *
   * <p>The method retrieves the poll, verifies that the user has update permissions,
   * marks the poll as deleted, and returns a localized response indicating the deletion status.</p>
   *
   * @param deletePollDto the dto of the poll to delete
   * @param user the user requesting the deletion
   * @return a {@link PollDeleteResponse} indicating whether the poll was successfully deleted
   * @throws PollNotFoundException if the poll with the given ID does not exist
   */
  @Override
  @Transactional
  public PollDeleteResponse deletePoll(final DeletePollDto deletePollDto, final RegisteredUser user) throws PollNotFoundException {
    final Long pollId = deletePollDto.getPollId();
    final Poll poll = pollCommonService.findPollById(pollId);

    pollCommonService.checkUpdatePermission(poll, user.toMember());
    poll.delete();

    final IsDeletedInfo isDeletedInfo = unifiedMapper.toIsDeletedInfo(poll.isDeleted());
    final PollDeleteResponse pollDeleteResponse = PollDeleteResponse.of(pollId, isDeletedInfo);

    return localizer.of(pollDeleteResponse);
  }

}