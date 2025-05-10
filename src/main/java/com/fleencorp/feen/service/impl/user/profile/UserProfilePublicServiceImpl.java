package com.fleencorp.feen.service.impl.user.profile;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.exception.member.MemberNotFoundException;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.mapper.contact.ContactMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.mapper.stream.StreamMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.info.user.profile.*;
import com.fleencorp.feen.model.request.search.UserProfileSearchRequest;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.response.user.UserResponse;
import com.fleencorp.feen.model.response.user.profile.UserProfileResponse;
import com.fleencorp.feen.model.search.chat.space.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.model.search.stream.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.model.search.stream.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.chat.space.ChatSpaceParticipationRepository;
import com.fleencorp.feen.repository.social.BlockUserRepository;
import com.fleencorp.feen.repository.stream.StreamParticipationRepository;
import com.fleencorp.feen.repository.user.FollowerRepository;
import com.fleencorp.feen.service.social.ContactService;
import com.fleencorp.feen.service.user.MemberService;
import com.fleencorp.feen.service.user.UserProfilePublicService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;
import static com.fleencorp.feen.util.CommonUtil.allNonNull;
import static java.util.Objects.nonNull;

@Service
public class UserProfilePublicServiceImpl implements UserProfilePublicService {

  private final ContactService contactService;
  private final MemberService memberService;
  private final FollowerRepository followerRepository;
  private final BlockUserRepository blockUserRepository;
  private final ChatSpaceParticipationRepository chatSpaceParticipationRepository;
  private final StreamParticipationRepository streamParticipationRepository;
  private final ChatSpaceMapper chatSpaceMapper;
  private final ContactMapper contactMapper;
  private final StreamMapper streamMapper;
  private final ToInfoMapper toInfoMapper;
  private final Localizer localizer;

  /**
   * Constructs a {@code UserProfilePublicServiceImpl} with required dependencies.
   *
   * <p>This constructor wires in services, repositories, and mappers needed
   * to fetch and transform user profile-related data for public exposure.</p>
   *
   * <p>These include services for contacts and members, repositories for
   * followers, block lists, chat space and stream participation, and mappers
   * for converting entities to DTOs or response models.</p>
   *
   * @param contactService service for managing contact-related operations
   * @param memberService service for handling member operations
   * @param followerRepository repository for accessing follower data
   * @param blockUserRepository repository for block relationships
   * @param chatSpaceParticipationRepository repository for chat participation
   * @param streamParticipationRepository repository for stream participation
   * @param chatSpaceMapper mapper for chat space objects
   * @param contactMapper mapper for contact entities
   * @param streamMapper mapper for stream entities
   * @param toInfoMapper mapper to convert domain objects to info DTOs
   * @param localizer component to resolve localized messages
   */
  public UserProfilePublicServiceImpl(
      final ContactService contactService,
      final MemberService memberService,
      final FollowerRepository followerRepository,
      final BlockUserRepository blockUserRepository,
      final ChatSpaceParticipationRepository chatSpaceParticipationRepository,
      final StreamParticipationRepository streamParticipationRepository,
      final ChatSpaceMapper chatSpaceMapper,
      final ContactMapper contactMapper,
      final StreamMapper streamMapper,
      final ToInfoMapper toInfoMapper,
      final Localizer localizer) {
    this.contactService = contactService;
    this.memberService = memberService;
    this.followerRepository = followerRepository;
    this.blockUserRepository = blockUserRepository;
    this.chatSpaceParticipationRepository = chatSpaceParticipationRepository;
    this.streamParticipationRepository = streamParticipationRepository;
    this.chatSpaceMapper = chatSpaceMapper;
    this.contactMapper = contactMapper;
    this.streamMapper = streamMapper;
    this.toInfoMapper = toInfoMapper;
    this.localizer = localizer;
  }

  /**
   * Retrieves the profile of the target user from the perspective of the currently authenticated user.
   * It includes relationship metadata (e.g., block, follow, contact eligibility) and mutual stream/chat space history.
   *
   * <p>The result is a localized {@link UserProfileResponse} populated with created streams, mutually attended streams,
   * shared chat spaces, and user relationship info. Pagination is limited to the first 10 entries per section.</p>
   *
   * @param userProfileSearchRequest request object containing the target user's ID
   * @param user the currently authenticated user
   * @return a localized {@link UserProfileResponse} describing the target user's profile
   */
  @Override
  public UserProfileResponse getUserProfile(final UserProfileSearchRequest userProfileSearchRequest, final FleenUser user) throws MemberNotFoundException {
    final Long targetUserId = userProfileSearchRequest.getTargetUserId();
    final Member member = user.toMember();
    final Member targetMember = findMember(targetUserId);

    final UserProfileResponse userProfileResponse = UserProfileResponse.of();
    setProfileDetails(targetMember, userProfileResponse);
    setFollowerDetails(targetMember, userProfileResponse);
    setInfoDetails(member, targetMember, userProfileResponse);
    findAndSetSearchResultDetails(member, targetMember, userProfileResponse);

    return localizer.of(userProfileResponse);
  }

  /**
   * Sets basic profile details of the target member into the given {@link UserProfileResponse}.
   *
   * <p>Populates the user's username and full name.</p>
   *
   * @param targetMember the member whose profile details are being set
   * @param userProfileResponse the response object to populate with profile information
   */
  protected void setProfileDetails(final Member targetMember, final UserProfileResponse userProfileResponse) {
    final UserResponse userResponse = UserResponse.of(targetMember.getUsername(), targetMember.getFullName());
    userProfileResponse.setUser(userResponse);
  }

  /**
   * Populates follower-related details into the given {@link UserProfileResponse}.
   *
   * <p>Computes the total number of users following the target member and the total number the target member is following.
   * These counts are then mapped into localized {@link TotalFollowedInfo} and {@link TotalFollowingInfo} objects.</p>
   *
   * @param targetMember the member whose follower details are being retrieved
   * @param userProfileResponse the response object to populate with follower data
   */
  protected void setFollowerDetails(final Member targetMember, final UserProfileResponse userProfileResponse) {
    final long totalFollowed = followerRepository.countByFollowed(targetMember.getMemberId());
    final long totalFollowing = followerRepository.countByFollowing(targetMember.getMemberId());

    final TotalFollowedInfo totalFollowedInfo = toInfoMapper.toTotalFollowedInfo(totalFollowed, targetMember.getFullName());
    final TotalFollowingInfo totalFollowingInfo = toInfoMapper.toTotalFollowingInfo(totalFollowing, targetMember.getFullName());

    userProfileResponse.setTotalFollowedInfo(totalFollowedInfo);
    userProfileResponse.setTotalFollowingInfo(totalFollowingInfo);
  }

  /**
   * Populates the {@link UserProfileResponse} with relationship and contact-related details between the
   * viewing user and the target user. This includes block status, follow status, and contact request eligibility.
   *
   * <p>Each info object is contextualized with the target user's full name and added to the response.</p>
   *
   * @param member the user viewing the profile
   * @param targetMember the user whose profile is being viewed
   * @param userProfileResponse the response object to be enriched with the info details
   */
  protected void setInfoDetails(final Member member, final Member targetMember, final UserProfileResponse userProfileResponse) {
    final Long memberId = safeGetMemberId(member);
    final Long targetMemberId = safeGetMemberId(targetMember);
    final String targetMemberFullName = nonNull(targetMember) ? targetMember.getFullName() : null;

    final boolean isBlocked = isBlockedByTargetUser(memberId, targetMemberId);
    final boolean isFollowed = isFollowedByTargetUser(memberId, targetMemberId);
    final boolean isFollowing = isFollowingTargetUser(memberId, targetMemberId);

    final ContactRequestEligibilityInfo contactRequestEligibilityInfo = checkContactRequestEligibility(member, targetMember);
    final IsBlockedInfo isBlockedInfo = toInfoMapper.toIsBlockedInfo(isBlocked, targetMemberFullName);
    final IsFollowedInfo isFollowedInfo = toInfoMapper.toIsFollowedInfo(isFollowed, targetMemberFullName);
    final IsFollowingInfo isFollowingInfo = toInfoMapper.toIsFollowingInfo(isFollowing, targetMemberFullName);

    userProfileResponse.setContactRequestEligibilityInfo(contactRequestEligibilityInfo);
    userProfileResponse.setIsBlockedInfo(isBlockedInfo);
    userProfileResponse.setIsFollowingInfo(isFollowingInfo);
    userProfileResponse.setIsFollowedInfo(isFollowedInfo);
  }

  /**
   * Finds and sets search result details related to the target member into the provided {@link UserProfileResponse}.
   *
   * <p>This includes streams created by the target member, streams mutually attended by both members, and chat spaces
   * where both members are active participants. The information is fetched using repositories with a page size of 10
   * and only includes entities with active or approved statuses. The resulting data is converted into response views
   * and assigned to the user profile response.</p>
   *
   * @param member the requesting member
   * @param targetMember the member whose profile is being viewed
   * @param userProfileResponse the response object to populate with search-related information
   */
  protected void findAndSetSearchResultDetails(final Member member, final Member targetMember, final UserProfileResponse userProfileResponse) {
    final Long memberId = nonNull(member) && nonNull(member.getMemberId()) ? member.getMemberId() : null;
    final Long targetMemberId = nonNull(targetMember) ? targetMember.getMemberId() : null;
    final String targetMemberFullName = nonNull(targetMember) ? targetMember.getFullName() : null;

    final Pageable pageable = PageRequest.of(0, 10);
    final Page<FleenStream> userCreatedStreams = nonNull(targetMemberId)
      ? streamParticipationRepository.findStreamsCreatedByMember(targetMemberId, List.of(StreamStatus.ACTIVE), pageable)
      : Page.empty();

    final Page<FleenStream> mutualAttendedStreams = allNonNull(memberId, targetMemberId)
      ? streamParticipationRepository.findCommonPastAttendedStreams(memberId, targetMemberId, List.of(StreamAttendeeRequestToJoinStatus.APPROVED), List.of(StreamStatus.ACTIVE), pageable)
      : Page.empty();

    final Page<ChatSpace> mutualChatSpaceMemberships = allNonNull(memberId, targetMemberId)
      ? chatSpaceParticipationRepository.findCommonChatSpaces(memberId, targetMemberId, List.of(ChatSpaceRequestToJoinStatus.APPROVED), List.of(ChatSpaceStatus.ACTIVE), pageable)
      : Page.empty();

    setSearchResultDetails(userCreatedStreams, mutualAttendedStreams, mutualChatSpaceMemberships, targetMemberFullName, userProfileResponse);
  }

  /**
   * Sets the search result details for a user's profile, including created streams,
   * mutually attended streams, and shared chat spaces.
   *
   * <p>This method processes the results of the streams and chat spaces the user is associated with,
   * organizes them into response objects, and sets them in the provided {@link UserProfileResponse}.
   * The results are localized before being added to the response.</p>
   *
   * @param userCreatedStreams the streams created by the user
   * @param mutualAttendedStreams the streams attended by both the user and the target user
   * @param mutualChatSpaceMemberships the chat spaces shared between the user and the target user
   * @param targetUserFullName the full name of the target user (for display in the response)
   * @param userProfileResponse the response object to set the search results in
   */
  protected void setSearchResultDetails(final Page<FleenStream> userCreatedStreams, final Page<FleenStream> mutualAttendedStreams, final Page<ChatSpace> mutualChatSpaceMemberships, final String targetUserFullName, final UserProfileResponse userProfileResponse) {
    final Collection<StreamResponse> userCreatedStreamResponses = streamMapper.toStreamResponses(userCreatedStreams.getContent());
    final Collection<StreamResponse> mutualAttendedStreamResponses = streamMapper.toStreamResponses(mutualAttendedStreams.getContent());
    final Collection<ChatSpaceResponse> mutualChatSpaceMembershipResponses = chatSpaceMapper.toChatSpaceResponses(mutualChatSpaceMemberships.getContent());

    final SearchResultView userCreatedStreamsSearchResultView = toSearchResult(userCreatedStreamResponses, userCreatedStreams);
    final SearchResultView mutualStreamAttendanceSearchResultView = toSearchResult(mutualAttendedStreamResponses, mutualAttendedStreams);
    final SearchResultView mutualChatSpaceMembershipSearchResultView = toSearchResult(mutualChatSpaceMembershipResponses, mutualChatSpaceMemberships);

    final UserCreatedStreamsSearchResult userCreatedStreamsSearchResult = UserCreatedStreamsSearchResult.of(userCreatedStreamsSearchResultView, targetUserFullName);
    final MutualStreamAttendanceSearchResult mutualStreamAttendanceSearchResult = MutualStreamAttendanceSearchResult.of(mutualStreamAttendanceSearchResultView, targetUserFullName);
    final MutualChatSpaceMembershipSearchResult mutualChatSpaceMembershipSearchResult = MutualChatSpaceMembershipSearchResult.of(mutualChatSpaceMembershipSearchResultView, targetUserFullName);

    localizer.of(userCreatedStreamsSearchResult);
    localizer.of(mutualStreamAttendanceSearchResult);
    localizer.of(mutualChatSpaceMembershipSearchResult);

    userProfileResponse.setUserCreatedStreamsSearchResult(userCreatedStreamsSearchResult);
    userProfileResponse.setMutualStreamAttendanceSearchResult(mutualStreamAttendanceSearchResult);
    userProfileResponse.setMutualChatSpaceMembershipSearchResult(mutualChatSpaceMembershipSearchResult);
  }

  /**
   * Retrieves the member with the specified ID by delegating to the member service.
   *
   * @param memberId the ID of the member to find
   * @return the Member instance
   */
  protected Member findMember(final Long memberId) throws MemberNotFoundException {
    return memberService.findMember(memberId);
  }

  /**
   * Checks if the user with the given ID is blocked by the target user.
   * Returns true if a block relationship from the target to the user exists.
   *
   * @param userId the ID of the user to check
   * @param targetUserId the ID of the user who may have blocked the other
   * @return true if the user is blocked by the target, false otherwise
   */
  protected boolean isBlockedByTargetUser(final Long userId, final Long targetUserId) {
    return allNonNull(userId, targetUserId) && blockUserRepository.findByInitiatorIdAndRecipientId(targetUserId, userId).isPresent();
  }

  /**
   * Determines whether the user with the given ID is following the target user.
   * Returns true if the user has initiated a following relationship with the target.
   *
   * @param userId the ID of the follower
   * @param targetUserId the ID of the followed user
   * @return true if following exists, false otherwise
   */
  protected boolean isFollowingTargetUser(final Long userId, final Long targetUserId) {
    return allNonNull(userId, targetUserId) && followerRepository.findByFollowingIdAndFollowedId(userId, targetUserId).isPresent();
  }

  /**
   * Determines whether the target user is following the given user.
   * Returns true if the target has initiated a following relationship with the user.
   *
   * @param userId the ID of the user being followed
   * @param targetUserId the ID of the follower
   * @return true if the target follows the user, false otherwise
   */
  protected boolean isFollowedByTargetUser(final Long userId, final Long targetUserId) {
    return allNonNull(userId, targetUserId) && followerRepository.findByFollowingIdAndFollowedId(targetUserId, userId).isPresent();
  }

  /**
   * Checks whether the requesting member is eligible to send a contact request to the target member.
   *
   * <p>If both members and their IDs are not null, eligibility is determined through the {@code contactService}.
   * Otherwise, an ineligible response is returned using the {@code contactMapper}.</p>
   *
   * @param member the member attempting to send a contact request
   * @param targetMember the member who would receive the contact request
   * @return a {@link ContactRequestEligibilityInfo} indicating whether the contact request is permitted
   */
  protected ContactRequestEligibilityInfo checkContactRequestEligibility(final Member member, final Member targetMember) {
    return allNonNull(member, targetMember, member.getMemberId(), targetMember.getMemberId())
      ? contactService.checkContactRequestEligibility(member, targetMember)
      : contactMapper.toEligibilityInfo(false);
  }

  /**
   * Safely retrieves the ID of the given member.
   *
   * <p>If the member or the member's ID is {@code null}, this method returns {@code null}.
   * Otherwise, it returns the member's ID.</p>
   *
   * @param member the member whose ID is to be retrieved
   * @return the member ID, or {@code null} if unavailable
   */
  private Long safeGetMemberId(final Member member) {
    return nonNull(member) && nonNull(member.getMemberId()) ? member.getMemberId() : null;
  }

}

