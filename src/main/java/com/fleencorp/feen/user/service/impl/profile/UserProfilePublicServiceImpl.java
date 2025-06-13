package com.fleencorp.feen.user.service.impl.profile;

import com.fleencorp.base.model.request.search.SearchRequest;
import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.attendee.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.contact.mapper.ContactMapper;
import com.fleencorp.feen.contact.service.ContactService;
import com.fleencorp.feen.mapper.common.UnifiedMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.domain.stream.FleenStream;
import com.fleencorp.feen.model.info.contact.ContactRequestEligibilityInfo;
import com.fleencorp.feen.model.info.user.profile.*;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import com.fleencorp.feen.model.response.stream.StreamResponse;
import com.fleencorp.feen.model.search.chat.space.mutual.MutualChatSpaceMembershipSearchResult;
import com.fleencorp.feen.model.search.social.follower.follower.FollowerSearchResult;
import com.fleencorp.feen.model.search.social.follower.following.FollowingSearchResult;
import com.fleencorp.feen.model.search.stream.common.UserCreatedStreamsSearchResult;
import com.fleencorp.feen.model.search.stream.mutual.MutualStreamAttendanceSearchResult;
import com.fleencorp.feen.repository.social.BlockUserRepository;
import com.fleencorp.feen.repository.user.FollowerRepository;
import com.fleencorp.feen.service.chat.space.ChatSpaceOperationsService;
import com.fleencorp.feen.service.social.FollowerService;
import com.fleencorp.feen.service.stream.StreamOperationsService;
import com.fleencorp.feen.user.exception.MemberNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.response.UserProfileResponse;
import com.fleencorp.feen.user.model.response.UserResponse;
import com.fleencorp.feen.user.model.search.UserProfileSearchRequest;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.service.MemberService;
import com.fleencorp.feen.user.service.profile.UserProfilePublicService;
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

  private final ChatSpaceOperationsService chatSpaceOperationsService;
  private final ContactService contactService;
  private final FollowerService followerService;
  private final MemberService memberService;
  private final FollowerRepository followerRepository;
  private final StreamOperationsService streamOperationsService;
  private final BlockUserRepository blockUserRepository;
  private final ContactMapper contactMapper;
  private final UnifiedMapper unifiedMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@code UserProfilePublicServiceImpl}, responsible for handling
   * public-facing user profile operations such as viewing streams, contacts,
   * chat spaces, and follow/block status.
   *
   * @param chatSpaceOperationsService service for managing chat space-related operations
   * @param contactService service for retrieving and managing user contacts
   * @param followerService service for managing user follows
   * @param memberService service for accessing member-related data and actions
   * @param streamOperationsService service for managing user streams and related activities
   * @param followerRepository repository for accessing and managing follower relationships
   * @param blockUserRepository repository for managing blocked user relationships
   * @param contactMapper mapper for contact related features
   * @param unifiedMapper general-purpose mapper for DTO and entity transformations
   * @param localizer utility for resolving localized text responses
   */
  public UserProfilePublicServiceImpl(
      final ChatSpaceOperationsService chatSpaceOperationsService,
      final ContactService contactService,
      final FollowerService followerService,
      final MemberService memberService,
      final StreamOperationsService streamOperationsService,
      final FollowerRepository followerRepository,
      final BlockUserRepository blockUserRepository,
      final ContactMapper contactMapper,
      final UnifiedMapper unifiedMapper,
      final Localizer localizer) {
    this.chatSpaceOperationsService = chatSpaceOperationsService;
    this.contactService = contactService;
    this.followerService = followerService;
    this.memberService = memberService;
    this.streamOperationsService = streamOperationsService;
    this.followerRepository = followerRepository;
    this.blockUserRepository = blockUserRepository;
    this.contactMapper = contactMapper;
    this.unifiedMapper = unifiedMapper;
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
  public UserProfileResponse getUserProfile(final UserProfileSearchRequest userProfileSearchRequest, final RegisteredUser user) throws MemberNotFoundException {
    final Long targetUserId = userProfileSearchRequest.getTargetUserId();
    final Member member = user.toMember();
    final Member targetMember = findMember(targetUserId);

    final UserProfileResponse userProfileResponse = UserProfileResponse.of();
    setProfileDetails(targetMember, userProfileResponse);
    setFollowerInfoDetails(targetMember, userProfileResponse);
    setInfoDetails(member, targetMember, userProfileResponse);
    findAndSetSearchResultDetails(member, targetMember, userProfileResponse);
    findAndSetFollowSearchResultDetails(targetMember, userProfileResponse);

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
    final UserResponse userResponse = UserResponse.of(
      targetMember.getMemberId(),
      targetMember.getUsername(),
      targetMember.getFullName(),
      targetMember.getProfilePhotoUrl());
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
  protected void setFollowerInfoDetails(final Member targetMember, final UserProfileResponse userProfileResponse) {
    followerService.setFollowerDetails(targetMember, userProfileResponse);
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
    final IsBlockedInfo isBlockedInfo = unifiedMapper.toIsBlockedInfo(isBlocked, targetMemberFullName);
    final IsFollowedInfo isFollowedInfo = unifiedMapper.toIsFollowedInfo(isFollowed, targetMemberFullName);
    final IsFollowingInfo isFollowingInfo = unifiedMapper.toIsFollowingInfo(isFollowing, targetMemberFullName);

    userProfileResponse.setContactRequestEligibilityInfo(contactRequestEligibilityInfo);
    userProfileResponse.setIsBlockedInfo(isBlockedInfo);
    userProfileResponse.setIsFollowedInfo(isFollowedInfo);
    userProfileResponse.setIsFollowingInfo(isFollowingInfo);
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
      ? streamOperationsService.findStreamsCreatedByMember(targetMemberId, List.of(StreamStatus.ACTIVE), pageable)
      : Page.empty();

    final Page<FleenStream> mutualAttendedStreams = allNonNull(memberId, targetMemberId)
      ? streamOperationsService.findCommonPastAttendedStreams(memberId, targetMemberId, List.of(StreamAttendeeRequestToJoinStatus.APPROVED), List.of(StreamStatus.ACTIVE), pageable)
      : Page.empty();

    final Page<ChatSpace> mutualChatSpaceMemberships = allNonNull(memberId, targetMemberId)
      ? chatSpaceOperationsService.findCommonChatSpaces(memberId, targetMemberId, List.of(ChatSpaceRequestToJoinStatus.APPROVED), List.of(ChatSpaceStatus.ACTIVE), pageable)
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
    final Collection<StreamResponse> userCreatedStreamResponses = unifiedMapper.toStreamResponses(userCreatedStreams.getContent());
    final Collection<StreamResponse> mutualAttendedStreamResponses = unifiedMapper.toStreamResponses(mutualAttendedStreams.getContent());
    final Collection<ChatSpaceResponse> mutualChatSpaceMembershipResponses = unifiedMapper.toChatSpaceResponses(mutualChatSpaceMemberships.getContent());

    final SearchResult userCreatedStreamsSearchResultView = toSearchResult(userCreatedStreamResponses, userCreatedStreams);
    final SearchResult mutualStreamAttendanceSearchResultView = toSearchResult(mutualAttendedStreamResponses, mutualAttendedStreams);
    final SearchResult mutualChatSpaceMembershipSearchResultView = toSearchResult(mutualChatSpaceMembershipResponses, mutualChatSpaceMemberships);

    final UserCreatedStreamsSearchResult userCreatedStreamsSearchResult = UserCreatedStreamsSearchResult.of(userCreatedStreamsSearchResultView, targetUserFullName);
    final MutualChatSpaceMembershipSearchResult mutualChatSpaceMembershipSearchResult = MutualChatSpaceMembershipSearchResult.of(mutualChatSpaceMembershipSearchResultView, targetUserFullName);
    final MutualStreamAttendanceSearchResult mutualStreamAttendanceSearchResult = MutualStreamAttendanceSearchResult.of(mutualStreamAttendanceSearchResultView, targetUserFullName);

    localizer.of(userCreatedStreamsSearchResult);
    localizer.of(mutualStreamAttendanceSearchResult);
    localizer.of(mutualChatSpaceMembershipSearchResult);

    userProfileResponse.setMutualChatSpaceMembershipSearchResult(mutualChatSpaceMembershipSearchResult);
    userProfileResponse.setMutualStreamAttendanceSearchResult(mutualStreamAttendanceSearchResult);
    userProfileResponse.setUserCreatedStreamsSearchResult(userCreatedStreamsSearchResult);
  }

  /**
   * Finds and sets follower and following search result details for the given member, and attaches them to the user profile response.
   *
   * <p>This method initializes a new {@link SearchRequest} and retrieves both followers and followings of the given {@code member}.
   * The results are localized and then set on the provided {@code userProfileResponse}.</p>
   *
   * @param member               the member whose follower and following data is being retrieved
   * @param userProfileResponse  the response object to populate with follower and following search results
   */
  protected void findAndSetFollowSearchResultDetails(final Member member, final UserProfileResponse userProfileResponse) {
    final SearchRequest searchRequest = new SearchRequest();
    final RegisteredUser user = RegisteredUser.of(member.getMemberId());

    final FollowerSearchResult followerSearchResult = followerService.getFollowers(searchRequest, user);
    final FollowingSearchResult followingSearchResult = followerService.getFollowings(searchRequest, user);

    localizer.of(followerSearchResult);
    localizer.of(followingSearchResult);

    userProfileResponse.setFollowerSearchResult(followerSearchResult);
    userProfileResponse.setFollowingSearchResult(followingSearchResult);
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
    return allNonNull(member, targetMember)
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

