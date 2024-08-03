package com.fleencorp.feen.repository.share;

import com.fleencorp.feen.constant.share.ShareContactRequestStatus;
import com.fleencorp.feen.model.domain.share.ShareContactRequest;
import com.fleencorp.feen.model.domain.user.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShareContactRequestRepository extends JpaRepository<ShareContactRequest, Long> {

  Page<ShareContactRequest> findByInitiatorAndShareContactRequestStatus(Member member, ShareContactRequestStatus shareContactRequestStatus, Pageable pageable);

  Page<ShareContactRequest> findByInitiatorAndIsExpected(Member member, Boolean isExpected, Pageable pageable);

  Optional<ShareContactRequest> findByShareContactRequestIdAndInitiator(Long id, Member member);
}
