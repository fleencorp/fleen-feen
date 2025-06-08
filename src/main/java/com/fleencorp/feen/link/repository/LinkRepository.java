package com.fleencorp.feen.link.repository;

import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.model.domain.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {

  @Query("SELECT l FROM Link l WHERE l.chatSpaceId = :chatSpaceId")
  Page<Link> findByChatSpaceId(@Param("chatSpaceId") Long chatSpaceId, Pageable pageable);

  List<Link> findByChatSpaceId(Long chatSpaceId);

  @Query("SELECT l FROM Link l WHERE l.chatSpaceId = :chatSpaceId AND l.linkType IN (:linkTypes)")
  List<Link> findByChatSpaceIdAndLinkType(@Param("chatSpaceId") Long chatSpaceId, @Param("linkTypes") List<LinkType> linkType);

}
