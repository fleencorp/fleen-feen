package com.fleencorp.feen.repository.social;

import com.fleencorp.feen.constant.social.BlockStatus;
import com.fleencorp.feen.model.domain.social.BlockUser;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlockUserRepository extends JpaRepository<BlockUser, Long> {

  Optional<BlockUser> findByRecipient(Member recipient);

  @Query("SELECT bu FROM BlockUser bu WHERE bu.initiator = :recipient AND bu.blockStatus = :blockStatus")
  Page<BlockUser> findByInitiatorAndBlockStatus(@Param("recipient") Member recipient, @Param("blockStatus") BlockStatus blockStatus, Pageable pageable);
}
