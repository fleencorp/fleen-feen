package com.fleencorp.feen.chat.space.repository.member;

import com.fleencorp.feen.chat.space.model.domain.ChatSpaceMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSpaceMemberRepository extends JpaRepository<ChatSpaceMember, Long> {}
