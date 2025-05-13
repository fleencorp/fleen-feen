package com.fleencorp.feen.repository.chat.space.member;

import com.fleencorp.feen.model.domain.chat.ChatSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSpaceMemberRepository extends JpaRepository<ChatSpaceMember, Long> {}
