package com.fleencorp.feen.repository.social;

import com.fleencorp.feen.constant.social.ShareContactRequestStatus;
import com.fleencorp.feen.model.domain.social.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ShareContactRequestRepository extends JpaRepository<ShareContactRequest, Long> {

  Optional<ShareContactRequest> findByShareContactRequestIdAndInitiator(Long id, Member member);

  // Find expected requests made by a specific member (initiator) where isExpected is true
  @EntityGraph(attributePaths = "recipient")
  @Query("SELECT scr FROM ShareContactRequest scr WHERE scr.initiator = :initiator AND scr.isExpected = :isExpected")
  Page<ShareContactRequest> findExpectedRequestsMadeByMember(@Param("initiator") Member initiator, @Param("isExpected") Boolean isExpected, Pageable pageable);

  // Find expected requests made to a specific member (recipient) where isExpected is true
  @EntityGraph(attributePaths = "initiator")
  @Query("SELECT scr FROM ShareContactRequest scr WHERE scr.recipient = :recipient AND scr.isExpected = :isExpected")
  Page<ShareContactRequest> findExpectedRequestsMadeToMember(@Param("recipient") Member recipient, @Param("isExpected") Boolean isExpected, Pageable pageable);

  // Find requests sent by a specific member (initiator)
  @EntityGraph(attributePaths = "recipient")
  @Query("SELECT scr FROM ShareContactRequest scr WHERE scr.initiator = :initiator AND scr.requestStatus = :requestStatus")
  Page<ShareContactRequest> findRequestsSentByMember(@Param("initiator") Member initiator, @Param("requestStatus") ShareContactRequestStatus shareContactRequestStatus, Pageable pageable);

  // Find requests made to a specific member (recipient)
  @EntityGraph(attributePaths = "initiator")
  @Query("SELECT scr FROM ShareContactRequest scr WHERE scr.recipient = :recipient AND scr.requestStatus = :requestStatus")
  Page<ShareContactRequest> findRequestsMadeToMember(@Param("recipient") Member recipient, @Param("requestStatus") ShareContactRequestStatus shareContactRequestStatus, Pageable pageable);
}
