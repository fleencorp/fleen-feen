package com.fleencorp.feen.poll.service.impl;

import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotAnAdminException;
import com.fleencorp.feen.chat.space.exception.core.ChatSpaceNotFoundException;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.chat.space.service.core.ChatSpaceOperationsService;
import com.fleencorp.feen.common.model.info.IsDeletedInfo;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.poll.constant.core.PollParentType;
import com.fleencorp.feen.poll.exception.poll.PollNotFoundException;
import com.fleencorp.feen.poll.mapper.PollUnifiedMapper;
import com.fleencorp.feen.poll.mapper.poll.PollMapper;
import com.fleencorp.feen.poll.model.domain.Poll;
import com.fleencorp.feen.poll.model.dto.AddPollDto;
import com.fleencorp.feen.poll.model.dto.DeletePollDto;
import com.fleencorp.feen.poll.model.holder.PollParentDetailHolder;
import com.fleencorp.feen.poll.model.response.PollCreateResponse;
import com.fleencorp.feen.poll.model.response.PollDeleteResponse;
import com.fleencorp.feen.poll.model.response.core.PollResponse;
import com.fleencorp.feen.poll.service.PollCommonService;
import com.fleencorp.feen.poll.service.PollOperationsService;
import com.fleencorp.feen.poll.service.PollService;
import com.fleencorp.feen.shared.security.RegisteredUser;
import com.fleencorp.feen.stream.exception.core.StreamNotCreatedByUserException;
import com.fleencorp.feen.stream.exception.core.StreamNotFoundException;
import com.fleencorp.feen.stream.model.domain.FleenStream;
import com.fleencorp.feen.stream.service.common.StreamOperationsService;
import com.fleencorp.feen.user.exception.member.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PollServiceImpl implements PollService {

  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final MemberService memberService;
  private final StreamOperationsService streamOperationsService;
  private final PollCommonService pollCommonService;
  private final PollOperationsService pollOperationsService;
  private final PollMapper pollMapper;
  private final UnifiedMapper unifiedMapper;
  private final PollUnifiedMapper pollUnifiedMapper;
  private final Localizer localizer;

  public PollServiceImpl(
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final MemberService memberService,
      final PollCommonService pollCommonService,
      final PollOperationsService pollOperationsService,
      final StreamOperationsService streamOperationsService,
      final PollMapper pollMapper,
      final UnifiedMapper unifiedMapper,
      final PollUnifiedMapper pollUnifiedMapper,
      final Localizer localizer) {
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.memberService = memberService;
    this.pollCommonService = pollCommonService;
    this.pollOperationsService = pollOperationsService;
    this.streamOperationsService = streamOperationsService;
    this.pollMapper = pollMapper;
    this.unifiedMapper = unifiedMapper;
    this.pollUnifiedMapper = pollUnifiedMapper;
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
   * @throws ChatSpaceNotAnAdminException if the member is not an admin or creator of the chat space
   * @throws StreamNotFoundException if the specified stream does not exist
   * @throws StreamNotCreatedByUserException if the member is not the organizer of the stream
   */
  @Override
  @Transactional
  public PollCreateResponse addPoll(final AddPollDto addPollDto, final RegisteredUser user)
    throws MemberNotFoundException, ChatSpaceNotFoundException, ChatSpaceNotAnAdminException,
      StreamNotFoundException, StreamNotCreatedByUserException {
    final Member member = memberService.findMember(user.getId());

    final PollParentDetailHolder parentDetailHolder = findAndValidateParent(addPollDto, member);
    final String parentTitle = parentDetailHolder.parentTitle();
    final ChatSpace chatSpace = parentDetailHolder.chatSpace();
    final FleenStream stream = parentDetailHolder.stream();

    final Poll poll = addPollDto.toPoll(member, parentTitle, chatSpace, stream);
    pollOperationsService.save(poll);

    final PollResponse response = pollUnifiedMapper.toPollResponse(poll);
    final PollCreateResponse createResponse = PollCreateResponse.of(poll.getPollId(), response);

    return localizer.of(createResponse);
  }

  /**
   * Determines and validates the parent entity (stream or chat space) for a poll based on the given {@link AddPollDto}.
   *
   * <p>This method checks if a parent is specified in the DTO. If a parent exists, it validates the parent
   * according to its type—either a stream or a chat space. Validation ensures the given member has the appropriate
   * permission (organizer or admin). If no parent is specified or the type is unrecognized, an empty
   * {@link PollParentDetailHolder} is returned.</p>
   *
   * @param addPollDto the DTO containing poll creation data, including parent information
   * @param member the member attempting to create the poll
   * @return an {@link PollParentDetailHolder} representing the validated parent (stream or chat space), or empty if none
   * @throws MemberNotFoundException if the member is not found
   * @throws ChatSpaceNotFoundException if the specified chat space does not exist
   * @throws ChatSpaceNotAnAdminException if the member is not an admin or creator of the chat space
   * @throws StreamNotFoundException if the specified stream does not exist
   * @throws StreamNotCreatedByUserException if the member is not the organizer of the stream
   */
  protected PollParentDetailHolder findAndValidateParent(final AddPollDto addPollDto, final Member member)
    throws MemberNotFoundException, ChatSpaceNotFoundException, ChatSpaceNotAnAdminException,
      StreamNotFoundException, StreamNotCreatedByUserException {

    final Long parentId = addPollDto.getParentId();
    final PollParentType parentType = addPollDto.getParentType();

    final ChatSpace chatSpace = findAndValidateChatSpaceParent(parentId, member);
    final FleenStream stream = findAndValidateStreamParent(parentId, member);

    return PollParentDetailHolder.of(chatSpace, stream, parentType);
  }

  /**
   * Finds a {@link FleenStream} by its ID and validates that the given {@link Member}
   * is the organizer of the stream.
   *
   * <p>The method retrieves the stream, checks that the requesting member is the organizer,
   * and returns the validated stream instance.</p>
   *
   * @param streamId the ID of the stream to find
   * @param member the member requesting access
   * @return the validated {@link FleenStream}
   * @throws StreamNotFoundException if no stream exists with the given ID
   * @throws StreamNotCreatedByUserException if the given member is not the organizer of the stream
   */
  protected FleenStream findAndValidateStreamParent(final Long streamId, final Member member)
      throws StreamNotFoundException, StreamNotCreatedByUserException {
    final FleenStream stream = streamOperationsService.findStream(streamId);
    stream.checkIsOrganizer(member.getMemberId());

    return stream;
  }

  /**
   * Finds a {@link ChatSpace} by its ID and validates that the given {@link Member}
   * is either the creator or an admin of the chat space.
   *
   * <p>The method retrieves the chat space, verifies that the requesting member has the required
   * permissions, and returns the validated chat space instance.</p>
   *
   * @param chatSpaceId the ID of the chat space to find
   * @param member the member requesting access
   * @return the validated {@link ChatSpace}
   * @throws ChatSpaceNotFoundException if no chat space exists with the given ID
   * @throws ChatSpaceNotAnAdminException if the given member is neither the creator nor an admin
   */
  protected ChatSpace findAndValidateChatSpaceParent(final Long chatSpaceId, final Member member)
      throws ChatSpaceNotFoundException, ChatSpaceNotAnAdminException {
    final ChatSpace chatSpace = chatSpaceOperationsService.findChatSpace(chatSpaceId);
    chatSpaceOperationsService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);

    return chatSpace;
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