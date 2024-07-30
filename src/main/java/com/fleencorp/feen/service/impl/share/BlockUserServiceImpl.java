package com.fleencorp.feen.service.impl.share;

import com.fleencorp.base.model.view.search.SearchResultView;
import com.fleencorp.feen.constant.share.BlockStatus;
import com.fleencorp.feen.exception.base.FailedOperationException;
import com.fleencorp.feen.exception.user.UserNotFoundException;
import com.fleencorp.feen.model.domain.share.BlockUser;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.dto.share.BlockUserDto;
import com.fleencorp.feen.model.request.search.share.BlockUserSearchRequest;
import com.fleencorp.feen.model.response.share.BlockUserStatusResponse;
import com.fleencorp.feen.model.response.share.BlockedUserResponse;
import com.fleencorp.feen.model.security.FleenUser;
import com.fleencorp.feen.repository.share.BlockUserRepository;
import com.fleencorp.feen.repository.user.MemberRepository;
import com.fleencorp.feen.service.share.BlockedUserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.fleencorp.base.util.FleenUtil.toSearchResult;

@Service
public class BlockUserServiceImpl implements BlockedUserService {

  private final BlockUserRepository blockUserRepository;
  private final MemberRepository memberRepository;

  public BlockUserServiceImpl(
      final BlockUserRepository blockUserRepository,
      final MemberRepository memberRepository) {
    this.blockUserRepository = blockUserRepository;
    this.memberRepository = memberRepository;
  }

  @Override
  public SearchResultView findBlockedUsers(final FleenUser user, final BlockUserSearchRequest searchRequest) {
    final Page<BlockUser> page = blockUserRepository.findByInitiatorAndBlockStatus(user.toMember(), BlockStatus.BLOCKED, searchRequest.getPage());

    final List<BlockedUserResponse> views = getBlockedUsers(page.getContent());
    return toSearchResult(views, page);
  }

  public List<BlockedUserResponse> getBlockedUsers(final List<BlockUser> blockUsers) {
    return blockUsers
        .stream()
        .map(blockUser -> {
          final String fullName = blockUser.getRecipient().getFullName();
          final Long userId = blockUser.getRecipient().getMemberId();

          return BlockedUserResponse.of(fullName, userId);
        })
        .toList();
  }

  @Override
  public BlockUserStatusResponse blockOrUnblockUser(final FleenUser user, final BlockUserDto blockUserDto) {
    memberRepository.findById(user.getId())
      .orElseThrow(FailedOperationException::new);

    final Member userToBeBlockedOrUnblocked = memberRepository.findById(blockUserDto.getRecipientId())
      .orElseThrow(() -> new UserNotFoundException(blockUserDto.getRecipientId()));

    final Member initiator = Member.of(user.getId());
    final BlockStatus blockStatus = blockUserDto.getActualBlockStatus();

    final BlockUser blockUser = blockUserRepository.findByRecipient(userToBeBlockedOrUnblocked)
        .orElseGet(() -> BlockUser.builder()
            .initiator(initiator)
            .recipient(userToBeBlockedOrUnblocked)
            .blockStatus(blockStatus)
            .build());

    blockUserRepository.save(blockUser);
    return BlockUserStatusResponse.of(blockStatus);
  }
}
