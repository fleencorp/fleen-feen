package com.fleencorp.feen.mapper.impl.chat;

import com.fleencorp.feen.constant.chat.space.ChatSpaceRequestToJoinStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceStatus;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.constant.chat.space.member.ChatSpaceMemberRole;
import com.fleencorp.feen.constant.common.JoinStatus;
import com.fleencorp.feen.like.model.info.UserLikeInfo;
import com.fleencorp.feen.mapper.chat.ChatSpaceMapper;
import com.fleencorp.feen.mapper.chat.member.ChatSpaceMemberMapper;
import com.fleencorp.feen.mapper.impl.BaseMapper;
import com.fleencorp.feen.mapper.info.ToInfoMapper;
import com.fleencorp.feen.model.domain.chat.ChatSpace;
import com.fleencorp.feen.model.info.IsDeletedInfo;
import com.fleencorp.feen.model.info.JoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceStatusInfo;
import com.fleencorp.feen.model.info.chat.space.ChatSpaceVisibilityInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceMemberRoleInfo;
import com.fleencorp.feen.model.info.chat.space.member.ChatSpaceRequestToJoinStatusInfo;
import com.fleencorp.feen.model.info.chat.space.membership.*;
import com.fleencorp.feen.model.info.interaction.LikeCountInfo;
import com.fleencorp.feen.model.other.Organizer;
import com.fleencorp.feen.model.response.chat.space.base.ChatSpaceResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Utility class for mapping {@link ChatSpace} entities to their corresponding response DTOs.
 *
 * <p>This class provides static methods for converting {@code ChatSpace} objects and related entities
 * to response objects, facilitating the transformation of domain entities to DTOs used for API responses.</p>
 *
 * <p>It handles null checks and performs necessary data conversions to ensure the consistency
 * and validity of mapped data.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class ChatSpaceMapperImpl extends BaseMapper implements ChatSpaceMapper {

  private final ChatSpaceMemberMapper chatSpaceMemberMapper;
  private final ToInfoMapper toInfoMapper;

  /**
   * Constructs a new {@code ChatSpaceMapperImpl}, responsible for mapping chat space entities to DTOs.
   *
   * @param toInfoMapper the mapper for converting domain models to info DTOs
   * @param chatSpaceMemberMapper the mapper for transforming chat space member entities
   * @param messageSource the source for resolving localized messages, passed to the superclass
   */
  public ChatSpaceMapperImpl(
      final ToInfoMapper toInfoMapper,
      final ChatSpaceMemberMapper chatSpaceMemberMapper,
      final MessageSource messageSource) {
    super(messageSource);
    this.chatSpaceMemberMapper = chatSpaceMemberMapper;
    this.toInfoMapper = toInfoMapper;
  }

  /**
   * Converts a {@link ChatSpace} entry to a {@link ChatSpaceResponse}.
   *
   * <p>This method takes a {@link ChatSpace} entity as input and converts it
   * into a {@link ChatSpaceResponse} object. It sets various fields such as
   * the chat space ID, title, description, tags, guidelines, and member count.</p>
   *
   * <p>Additionally, it retrieves both the masked and unmasked space links,
   * active status, and the creation and update timestamps.</p>
   *
   * <p>It also maps the chat space visibility to a {@link ChatSpaceVisibilityInfo}
   * object and sets the organizer's information (name, email, and phone).</p>
   *
   * <p>Finally, it sets the request-to-join status, and the join status,
   * including localized messages for both statuses.</p>
   *
   * @param entry the {@link ChatSpace} entity to convert
   * @return a {@link ChatSpaceResponse} object populated with chat space details,
   *         or {@code null} if the input is null
   */
  @Override
  public ChatSpaceResponse toChatSpaceResponse(final ChatSpace entry) {
    if (nonNull(entry)) {
      final ChatSpaceResponse response = new ChatSpaceResponse();
      response.setId(entry.getChatSpaceId());
      response.setTitle(entry.getTitle());
      response.setDescription(entry.getDescription());
      response.setTags(entry.getTags());
      response.setGuidelinesOrRules(entry.getGuidelinesOrRules());
      response.setTotalMembers(entry.getTotalMembers());

      response.setSpaceLink(entry.getMaskedSpaceLink());
      response.setSpaceLinkUnMasked(entry.getSpaceLink());
      response.setOrganizerId(entry.getOrganizerId());

      response.setCreatedOn(entry.getCreatedOn());
      response.setUpdatedOn(entry.getUpdatedOn());

      final ChatSpaceVisibility visibility = entry.getSpaceVisibility();
      final ChatSpaceVisibilityInfo visibilityInfo = ChatSpaceVisibilityInfo.of(visibility, translate(visibility.getMessageCode()));
      response.setVisibilityInfo(visibilityInfo);

      final ChatSpaceStatusInfo chatSpaceStatusInfo = toChatSpaceStatusInfo(entry.getStatus());
      response.setStatusInfo(chatSpaceStatusInfo);

      final IsDeletedInfo deletedInfo = toInfoMapper.toIsDeletedInfo(entry.getDeleted());
      response.setDeletedInfo(deletedInfo);

      final Organizer organizer = Organizer.of(entry.getOrganizerName(), entry.getOrganizerEmail(), entry.getOrganizerPhone());
      response.setOrganizer(organizer);

      final JoinStatus joinStatus = JoinStatus.byChatSpaceStatus(entry.isPrivate());
      final JoinStatusInfo joinStatusInfo = JoinStatusInfo.of(joinStatus, translate(joinStatus.getMessageCode()), translate(joinStatus.getMessageCode2()), translate(joinStatus.getMessageCode3()));

      final LikeCountInfo likeCountInfo = toInfoMapper.toLikeCountInfo(entry.getLikeCount());
      response.setLikeCountInfo(likeCountInfo);

      final IsAChatSpaceMemberInfo isAMemberInfo = chatSpaceMemberMapper.toIsAChatSpaceMemberInfo(false);
      final IsAChatSpaceAdminInfo isAAdminInfo = chatSpaceMemberMapper.toIsAChatSpaceAdminInfo(false);
      final IsChatSpaceMemberLeftInfo isChatSpaceMemberLeftInfo = chatSpaceMemberMapper.toIsChatSpaceMemberLeftInfo(false);
      final IsChatSpaceMemberRemovedInfo isChatSpaceMemberRemovedInfo = chatSpaceMemberMapper.toIsChatSpaceMemberRemovedInfo(false);
      final ChatSpaceMemberRoleInfo chatSpaceMemberRoleInfo = chatSpaceMemberMapper.toMemberRoleInfo(ChatSpaceMemberRole.MEMBER);
      final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = ChatSpaceRequestToJoinStatusInfo.of();

      final ChatSpaceMembershipInfo membershipInfo = ChatSpaceMembershipInfo.of(
        requestToJoinStatusInfo,
        joinStatusInfo,
        chatSpaceMemberRoleInfo,
        isAMemberInfo,
        isAAdminInfo,
        isChatSpaceMemberLeftInfo,
        isChatSpaceMemberRemovedInfo
      );
      response.setMembershipInfo(membershipInfo);

      final UserLikeInfo userLikeInfo = toInfoMapper.toLikeInfo(false);
      response.setUserLikeInfo(userLikeInfo);

      return response;
    }
    return null;
  }

  /**
   * Converts a {@link ChatSpace} entity to an approved {@link ChatSpaceResponse}.
   *
   * <p>This method creates a {@link ChatSpaceResponse} representation of the given {@link ChatSpace},
   * setting it to reflect an approved status for joining the chat space.</p>
   *
   * @param entry the {@link ChatSpace} entity to convert; must not be {@code null}.
   * @return a {@link ChatSpaceResponse} with approved request-to-join and join status information,
   *         or {@code null} if the input {@code entry} is {@code null}.
   */
  @Override
  public ChatSpaceResponse toChatSpaceResponseByAdminUpdate(final ChatSpace entry) {
    if (nonNull(entry)) {
      final ChatSpaceResponse chatSpaceResponse = toChatSpaceResponse(entry);
      final JoinStatus joinStatus = JoinStatus.joinedChatSpace();
      final ChatSpaceRequestToJoinStatus requestToJoinStatus = ChatSpaceRequestToJoinStatus.approved();

      setMembershipInfo(chatSpaceResponse, requestToJoinStatus, joinStatus, ChatSpaceMemberRole.ADMIN, true, true, false, false);
      return chatSpaceResponse;
    }
    return null;
  }

  /**
   * Converts a list of {@code ChatSpace} entities to a list of {@code ChatSpaceResponse} objects.
   *
   * <p>This method checks if the provided list of {@code ChatSpace} entries is non-null.
   * It filters out any null entries, maps each valid {@code ChatSpace} entity to a
   * {@code ChatSpaceResponse} object, and returns the resulting list. If the input list
   * is null, the method returns an empty list.</p>
   *
   * @param entries the list of {@code ChatSpace} entities to convert
   * @return a list of {@code ChatSpaceResponse} objects with the mapped data, or an empty list if the input is null
   **/
  @Override
  public List<ChatSpaceResponse> toChatSpaceResponses(final List<ChatSpace> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(this::toChatSpaceResponse)
        .toList();
    }
    return List.of();
  }

  /**
   * Sets the membership information for a given chat space.
   *
   * <p>This method populates the membership-related fields of the chat space
   * by constructing various membership status objects, including join status,
   * request to join status, member info, admin info, and membership activity info.</p>
   *
   * <p>If the `chatSpace` object is not null, it creates the following objects:
   * {@link JoinStatusInfo}, {@link ChatSpaceRequestToJoinStatusInfo},
   * {@link IsAChatSpaceMemberInfo}, {@link IsAChatSpaceAdminInfo},
   * {@link IsChatSpaceMemberLeftInfo}, and {@link IsChatSpaceMemberRemovedInfo}.
   * These objects are then combined into a {@link ChatSpaceMembershipInfo} object
   * which is set in the chat space.</p>
   *
   * @param chatSpace the chat space object to update with membership info
   * @param requestToJoinStatus the status of the user's request to join the chat space
   * @param joinStatus the current join status of the user in the chat space
   * @param chatSpaceMemberRole the role information of the chat space member
   * @param isAMember whether the user is currently a member of the chat space
   * @param isAdmin whether the user is an admin of the chat space
   */
  @Override
  public void setMembershipInfo(
      final ChatSpaceResponse chatSpace,
      final ChatSpaceRequestToJoinStatus requestToJoinStatus,
      final JoinStatus joinStatus,
      final ChatSpaceMemberRole chatSpaceMemberRole,
      final boolean isAMember,
      final boolean isAdmin,
      final boolean hasLeft,
      final boolean isRemoved) {
    if (nonNull(chatSpace)) {
      final JoinStatusInfo joinStatusInfo = chatSpaceMemberMapper.toJoinStatusInfo(chatSpace, joinStatus);
      final ChatSpaceRequestToJoinStatusInfo requestToJoinStatusInfo = chatSpaceMemberMapper.toRequestToJoinStatusInfo(chatSpace, requestToJoinStatus);
      final IsAChatSpaceMemberInfo isAChatSpaceMemberInfo = chatSpaceMemberMapper.toIsAChatSpaceMemberInfo(isAMember);
      final IsAChatSpaceAdminInfo isAChatSpaceAdminInfo = chatSpaceMemberMapper.toIsAChatSpaceAdminInfo(isAdmin);
      final IsChatSpaceMemberLeftInfo isChatSpaceMemberLeftInfo = chatSpaceMemberMapper.toIsChatSpaceMemberLeftInfo(hasLeft);
      final IsChatSpaceMemberRemovedInfo isChatSpaceMemberRemovedInfo = chatSpaceMemberMapper.toIsChatSpaceMemberRemovedInfo(isRemoved);
      final ChatSpaceMemberRoleInfo chatSpaceMemberRoleInfo = chatSpaceMemberMapper.toMemberRoleInfo(chatSpaceMemberRole);

      final ChatSpaceMembershipInfo chatSpaceMembershipInfo = ChatSpaceMembershipInfo.of(
        requestToJoinStatusInfo,
        joinStatusInfo,
        chatSpaceMemberRoleInfo,
        isAChatSpaceMemberInfo,
        isAChatSpaceAdminInfo,
        isChatSpaceMemberLeftInfo,
        isChatSpaceMemberRemovedInfo
      );

      chatSpace.setMembershipInfo(chatSpaceMembershipInfo);
    }
  }

  /**
   * Converts the provided {@link ChatSpaceStatus} into a {@link ChatSpaceStatusInfo} object.
   *
   * <p>This method takes a {@code ChatSpaceStatus} and translates its associated message codes.
   * The translated messages are then used to create a {@link ChatSpaceStatusInfo} object,
   * which contains the status and the translated messages for display or further processing.</p>
   *
   * @param status the {@link ChatSpaceStatus} to convert, containing message codes to be translated
   * @return a {@link ChatSpaceStatusInfo} containing the status and its translated messages
   */
  @Override
  public ChatSpaceStatusInfo toChatSpaceStatusInfo(final ChatSpaceStatus status) {
    return ChatSpaceStatusInfo.of(
      status,
      translate(status.getMessageCode()),
      translate(status.getMessageCode2()),
      translate(status.getMessageCode3()),
      translate(status.getMessageCode4()),
      translate(status.getMessageCode5())
    );
  }

}
