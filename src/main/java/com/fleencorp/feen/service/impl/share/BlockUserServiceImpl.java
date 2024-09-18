package com.fleencorp.feen.service.impl.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.share.BlockStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.model.domain.share.BlockUser;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.share.BlockUserDto;
import com.fleencorp.feen.model.request.search.share.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.share.block.BlockUserStatusResponse;
import com.fleencorp.feen.model.response.share.block.BlockedUserResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.share.BlockUserRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.share.BlockedUserService;
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
public class BlockUserServiceImpl implements BlockedUserService {

  private final BlockUserRepository blockUserRepository;
  private final MemberRepository memberRepository;

  /**
   * Constructs an instance of `BlockUserServiceImpl` with the specified repositories.
   *
   * @param blockUserRepository the repository for managing blocked users
   * @param memberRepository the repository for accessing member information
   */
  public BlockUserServiceImpl(
      final BlockUserRepository blockUserRepository,
      final MemberRepository memberRepository) {
    this.blockUserRepository = blockUserRepository;
    this.memberRepository = memberRepository;
  }

  /**
   * Finds blocked users based on the search criteria provided in the `BlockUserSearchRequest`.
   * It retrieves a paginated list of blocked users initiated by the specified user and converts them into a list of `BlockedUserResponse` objects.
   *
   * @param searchRequest the search criteria including pagination details
   * @param user the user who initiated the block action
   * @return a `SearchResultView` containing the list of blocked users and pagination details
   */
  @Override
  public SearchResultView findBlockedUsers(final BlockUserSearchRequest searchRequest, final FleenUser user) {
    final Page<BlockUser> page = blockUserRepository.findByInitiatorAndBlockStatus(user.toMember(), BlockStatus.BLOCKED, searchRequest.getPage());

    final List<BlockedUserResponse> views = getBlockedUsers(page.getContent());
    return toSearchResult(views, page);
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
    return blockUsers
        .stream()
        .filter(Objects::nonNull)
        .map(blockUser -> {
          final String fullName = blockUser.getRecipient().getFullName();
          final Long userId = blockUser.getRecipient().getMemberId();

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
    memberRepository.findById(user.getId())
      .orElseThrow(FailedOperationException::new);

    // Retrieve the user to be blocked or unblocked, or throw an exception if not found
    final Member userToBeBlockedOrUnblocked = memberRepository.findById(blockUserDto.getRecipientId())
      .orElseThrow(() -> new UserNotFoundException(blockUserDto.getRecipientId()));

    final Member initiator = Member.of(user.getId());
    final BlockStatus blockStatus = blockUserDto.getActualBlockStatus();

    // Find or create a BlockUser entity for the given recipient
    final BlockUser blockUser = blockUserRepository.findByRecipient(userToBeBlockedOrUnblocked)
        .orElseGet(() -> BlockUser.of(initiator, userToBeBlockedOrUnblocked, blockStatus));

    // Save the BlockUser entity to the repository
    blockUserRepository.save(blockUser);
    return BlockUserStatusResponse.of(blockStatus);
  }
}
