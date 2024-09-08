package com.fleencorp.feen.repository.share;

import com.fleencorp.feen.constant.share.BlockStatus;
import com.fleencorp.feen.model.domain.share.BlockUser;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {

  Optional<BlockUser> findByRecipient(Member recipient);

  Page<BlockUser> findByInitiatorAndBlockStatus(Member recipient, BlockStatus blockStatus, Pageable pageable);
}
