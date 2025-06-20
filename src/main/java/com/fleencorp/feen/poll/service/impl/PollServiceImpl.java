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
   * Creates and saves a new standalone {@link Poll} (not associated with a chat space or stream).
   *
   * <p>This method retrieves the {@link Member} from the given {@link RegisteredUser},
   * constructs the poll using the provided {@link AddPollDto}, saves it,
   * and returns a localized response containing the created poll.</p>
   *
   * @param addPollDto the DTO containing poll creation data
   * @param user the user creating the poll
   * @return a {@link PollCreateResponse} containing the newly created poll
   * @throws MemberNotFoundException if the user does not correspond to a valid member
   */
  @Override
  @Transactional
  public PollCreateResponse addPoll(final AddPollDto addPollDto, final RegisteredUser user) throws MemberNotFoundException {
    final Member member = memberService.findMember(user.getId());

    final Poll poll = addPollDto.toPoll(member);
    return saveAndGetPoll(poll);
  }
  /**
   * Creates and saves a new {@link Poll} within the specified {@link FleenStream}, if the user is the organizer.
   *
   * <p>This method retrieves the member and stream, verifies that the user is the stream organizer,
   * constructs the poll using the provided {@link AddPollDto}, and saves it.
   * Returns a localized response containing the newly created poll.</p>
   *
   * @param streamId the ID of the stream where the poll will be added
   * @param addPollDto the DTO containing poll creation data
   * @param user the user attempting to create the poll
   * @return a {@link PollCreateResponse} containing the newly created poll
   * @throws MemberNotFoundException if the user does not correspond to a valid member
   * @throws StreamNotFoundException if the stream does not exist
   * @throws StreamNotCreatedByUserException if the user is not the organizer of the stream
   */
  @Override
  @Transactional
  public PollCreateResponse streamAddPoll(final Long streamId, final AddPollDto addPollDto, final RegisteredUser user)
      throws MemberNotFoundException, StreamNotFoundException, StreamNotCreatedByUserException {
    final Member member = memberService.findMember(user.getId());
    final FleenStream stream = streamOperationsService.findStream(streamId);

    stream.checkIsOrganizer(user.getId());

    final Poll poll = addPollDto.toStreamPoll(stream, member);
    return saveAndGetPoll(poll);
  }

  /**
   * Creates and saves a new {@link Poll} within the specified {@link ChatSpace}, if the user has admin or creator privileges.
   *
   * <p>This method retrieves the member and chat space, verifies the user's permission to create polls in the chat space,
   * constructs the poll from the provided {@link AddPollDto}, and persists it. The result is returned as a localized response.</p>
   *
   * @param chatSpaceId the ID of the chat space where the poll will be added
   * @param addPollDto the DTO containing poll creation data
   * @param user the user attempting to create the poll
   * @return a {@link PollCreateResponse} containing the newly created poll
   * @throws MemberNotFoundException if the user does not correspond to a valid member
   * @throws ChatSpaceNotFoundException if the chat space does not exist
   * @throws NotAnAdminOfChatSpaceException if the user lacks permission to create polls in the chat space
   */
  @Override
  @Transactional
  public PollCreateResponse chatSpaceAddPoll(final Long chatSpaceId, final AddPollDto addPollDto, final RegisteredUser user)
      throws MemberNotFoundException, ChatSpaceNotFoundException, NotAnAdminOfChatSpaceException {
    final Member member = memberService.findMember(user.getId());
    final ChatSpace chatSpace = chatSpaceOperationsService.findChatSpace(chatSpaceId);

    chatSpaceService.verifyCreatorOrAdminOfChatSpace(chatSpace, member);

    final Poll poll = addPollDto.toChatSpacePoll(chatSpace, member);
    return saveAndGetPoll(poll);
  }

  /**
   * Saves the given {@link Poll} and returns a localized {@link PollCreateResponse}.
   *
   * <p>This method persists the poll using the poll operations service, maps the saved poll to a response DTO,
   * and wraps it in a localized create response.</p>
   *
   * @param poll the poll to save
   * @return a {@link PollCreateResponse} containing the saved poll's data
   */
  protected PollCreateResponse saveAndGetPoll(final Poll poll) {
    pollOperationsService.save(poll);

    final PollResponse response = pollMapper.toPollResponse(poll);
    final PollCreateResponse createResponse = PollCreateResponse.of(poll.getPollId(), response);

    return localizer.of(createResponse);
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