package com.fleencorp.feen.service.impl.social;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.constant.social.BlockStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.model.domain.social.BlockUser;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.social.block.BlockUserDto;
import com.fleencorp.feen.model.request.search.social.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.social.block.BlockUserStatusResponse;
import com.fleencorp.feen.model.response.social.block.BlockedUserResponse;
import com.fleencorp.feen.model.search.social.blocking.BlockingUserSearchResult;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.social.BlockUserRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.social.BlockUserService;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;

/**
 * Service implementation for managing blocked users.
 *
 * <p>This class provides methods to block or unblock users,
 * retrieve blocked user details, and handle related operations.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Service
public class BlockUserServiceImpl implements BlockUserService {

  private final BlockUserRepository blockUserRepository;
  private final MemberRepository memberRepository;
  private final Localizer localizer;

  /**
   * Constructs a new {@link BlockUserServiceImpl} instance with the required dependencies.
   *
   * <p>This constructor initializes the service by injecting the necessary repositories and the localized response service
   * to handle user blocking operations.</p>
   *
   * @param blockUserRepository  the {@link BlockUserRepository} used for managing block operations
   * @param memberRepository     the {@link MemberRepository} used for accessing member data
   * @param localizer    the {@link Localizer} service used to generate localized responses
   */
  public BlockUserServiceImpl(
      final BlockUserRepository blockUserRepository,
      final MemberRepository memberRepository,
      final Localizer localizer) {
    this.blockUserRepository = blockUserRepository;
    this.memberRepository = memberRepository;
    this.localizer = localizer;
  }

  /**
   * Finds blocked users based on the search criteria provided in the `BlockUserSearchRequest`.
   * It retrieves a paginated list of blocked users initiated by the specified user and converts them into a list of `BlockedUserResponse` objects.
   *
   * @param searchRequest the search criteria including pagination details
   * @param user the user who initiated the block action
   * @return a BlockingUserSearchResult containing the list of blocked users and pagination details
   */
  @Override
  public BlockingUserSearchResult findBlockedUsers(final BlockUserSearchRequest searchRequest, final FleenUser user) {
    final Page<BlockUser> page = blockUserRepository.findByInitiatorAndBlockStatus(user.toMember(), BlockStatus.BLOCKED, searchRequest.getPage());

    final List<BlockedUserResponse> blockedUserResponses = getBlockedUsers(page.getContent());
    // Create a search result
    final SearchResult searchResult = toSearchResult(blockedUserResponses, page);
    // Create a search result with the responses and pagination details
    final BlockingUserSearchResult blockingUserSearchResult = BlockingUserSearchResult.of(searchResult);
    // Return the search result
    return localizer.of(blockingUserSearchResult);
  }

  /**
   * Converts a list of `BlockUser` entities into a list of `BlockedUserResponse` objects.
   * Each `BlockUser` contains information about a recipient who has been blocked.
   * This method extracts the full name and member ID of the recipient and creates a response object.
   *
   * @param blockUsers the list of `BlockUser` entities to be processed
   * @return a list of `BlockedUserResponse` objects containing the full name and user ID of each blocked recipient
   */
  protected List<BlockedUserResponse> getBlockedUsers(final List<BlockUser> blockUsers) {
    return blockUsers.stream()
        .filter(Objects::nonNull)
        .map(blockUser -> {
          final String fullName = blockUser.getRecipientName();
          final Long userId = blockUser.getRecipientMemberId();

          return BlockedUserResponse.of(fullName, userId);
        })
        .toList();
  }

  /**
   * Blocks or unblocks a user based on the provided `BlockUserDto` and the action initiator.
   * It first checks if the initiator exists and then retrieves or creates a `BlockUser` entry for the user to be blocked or unblocked.
   * The method updates the block status and returns a response indicating the result of the operation.
   *
   * @param blockUserDto the data transfer object containing information about the block/unblock request
   * @param user the user initiating the block/unblock action
   * @return a `BlockUserStatusResponse` indicating the new block status
   * @throws FailedOperationException if the initiator user cannot be found
   * @throws UserNotFoundException if the recipient user to be blocked or unblocked cannot be found
   */
  @Override
  public BlockUserStatusResponse blockOrUnblockUser(final BlockUserDto blockUserDto, final FleenUser user) {
    // Validate the member or authenticated user details
    memberRepository.findById(user.getId())
      .orElseThrow(FailedOperationException::new);

    // Retrieve the user to be blocked or unblocked, or throw an exception if not found
    final Member userToBeBlockedOrUnblocked = memberRepository.findById(blockUserDto.getRecipientId())
      .orElseThrow(UserNotFoundException.of(blockUserDto.getRecipientId()));

    final Member initiator = Member.of(user.getId());
    final BlockStatus blockStatus = blockUserDto.getBlockStatus();

    // Find or create a BlockUser entity for the given recipient
    final BlockUser blockUser = blockUserRepository.findByRecipient(userToBeBlockedOrUnblocked)
        .orElseGet(() -> BlockUser.of(initiator, userToBeBlockedOrUnblocked, blockStatus));

    // Save the BlockUser entity to the repository
    blockUserRepository.save(blockUser);
    // Returned the localized response of the blocked user
    return localizer.of(BlockUserStatusResponse.of(blockStatus));
  }

  /**
   * Determines whether a block relationship exists between the viewer and the target member.
   *
   * <p>This method checks if the given {@code viewer} has blocked the {@code target}.</p>
   *
   * @param viewer the member who may have initiated the block
   * @param target the member who may have been blocked
   * @return {@code true} if the viewer has blocked the target, {@code false} otherwise
   */
  @Override
  public boolean existsByInitiatorAndRecipient(final Member viewer, final Member target) {
    return blockUserRepository.existsByInitiatorAndRecipient(viewer, target);
  }
}
