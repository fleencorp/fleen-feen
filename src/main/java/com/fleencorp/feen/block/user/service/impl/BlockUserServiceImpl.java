package com.fleencorp.feen.block.user.service.impl;

import com.fleencorp.base.model.view.search.SearchResult;
import com.fleencorp.feen.block.user.mapper.BlockUserMapper;
import com.fleencorp.feen.block.user.model.domain.BlockUser;
import com.fleencorp.feen.block.user.model.dto.BlockUserDto;
import com.fleencorp.feen.block.user.model.request.search.BlockUserSearchRequest;
import com.fleencorp.feen.block.user.model.response.BlockUserResponse;
import com.fleencorp.feen.block.user.model.response.BlockUserStatusResponse;
import com.fleencorp.feen.block.user.model.search.BlockingUserSearchResult;
import com.fleencorp.feen.block.user.repository.BlockUserRepository;
import com.fleencorp.feen.block.user.service.BlockUserService;
import com.fleencorp.feen.constant.social.BlockStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.user.exception.user.UserNotFoundException;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.security.RegisteredUser;
import com.fleencorp.feen.user.repository.MemberRepository;
import com.fleencorp.localizer.service.Localizer;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collection;

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
  private final BlockUserMapper blockUserMapper;
  private final Localizer localizer;

  /**
   * Constructs a new {@link BlockUserServiceImpl} instance with the required dependencies.
   *
   * <p>This constructor initializes the service by injecting the necessary repositories, mapper, and localization service
   * to support user blocking operations, member lookups, response mapping, and localized message generation.</p>
   *
   * @param blockUserRepository the {@link BlockUserRepository} used for managing block operations
   * @param memberRepository the {@link MemberRepository} used for accessing member data
   * @param blockUserMapper the {@link BlockUserMapper} used to map {@code BlockUser} entities to DTOs
   * @param localizer the {@link Localizer} service used to generate localized messages
   */
  public BlockUserServiceImpl(
      final BlockUserRepository blockUserRepository,
      final MemberRepository memberRepository,
      final BlockUserMapper blockUserMapper,
      final Localizer localizer) {
    this.blockUserRepository = blockUserRepository;
    this.memberRepository = memberRepository;
    this.blockUserMapper = blockUserMapper;
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
  public BlockingUserSearchResult findBlockedUsers(final BlockUserSearchRequest searchRequest, final RegisteredUser user) {
    final Page<BlockUser> page = blockUserRepository.findByInitiatorAndBlockStatus(user.toMember(), BlockStatus.BLOCKED, searchRequest.getPage());

    final Collection<BlockUserResponse> blockUserResponse = blockUserMapper.toBlockUserResponse(page.getContent());
    // Create a search result
    final SearchResult searchResult = toSearchResult(blockUserResponse, page);
    // Create a search result with the responses and pagination details
    final BlockingUserSearchResult blockingUserSearchResult = BlockingUserSearchResult.of(searchResult);
    // Return the search result
    return localizer.of(blockingUserSearchResult);
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
  public BlockUserStatusResponse blockOrUnblockUser(final BlockUserDto blockUserDto, final RegisteredUser user) {
    memberRepository.findById(user.getId()).orElseThrow(FailedOperationException::new);

    final BlockUser blockUser = blockOrUnblock(blockUserDto, user.toMember());
    blockUserRepository.save(blockUser);

    final BlockUserResponse blockUserResponse = blockUserMapper.toBlockUserResponse(blockUser);
    final BlockUserStatusResponse blockUserStatusResponse = BlockUserStatusResponse.of(blockUserResponse, blockUser.getBlockStatus());
    return localizer.of(blockUserStatusResponse);
  }

  /**
   * Retrieves an existing {@link BlockUser} entity for the specified recipient, or creates a new one
   * if none exists, based on the block status and initiator.
   *
   * <p>If the recipient user is not found, a {@link UserNotFoundException} is thrown. If the block record does not exist,
   * a new {@link BlockUser} entity is created with the given block status.</p>
   *
   * @param blockUserDto the DTO containing the recipient ID and the desired {@link BlockStatus}
   * @param initiator the {@link Member} initiating the block or unblock action
   * @return the existing or newly created {@link BlockUser} entity
   * @throws UserNotFoundException if the recipient user does not exist
   */
  protected BlockUser blockOrUnblock(final BlockUserDto blockUserDto, final Member initiator) {
    final Member userToBeBlockedOrUnblocked = memberRepository.findById(blockUserDto.getRecipientId())
      .orElseThrow(UserNotFoundException.of(blockUserDto.getRecipientId()));

    final BlockStatus blockStatus = blockUserDto.getBlockStatus();

    return blockUserRepository.findByRecipient(userToBeBlockedOrUnblocked)
      .orElseGet(() -> BlockUser.of(initiator, userToBeBlockedOrUnblocked, blockStatus));
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
