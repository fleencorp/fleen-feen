package com.fleencorp.feen.link.repository;

import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.link.model.domain.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LinkRepository extends JpaRepository<Link, Long> {

  @Query("SELECT l FROM Link l WHERE l.chatSpaceId = :chatSpaceId AND l.parentId = :chatSpaceId")
  Page<Link> findByChatSpaceId(@Param("chatSpaceId") Long chatSpaceId, Pageable pageable);

  @Query("SELECT l FROM Link l WHERE l.businessId = :businessId AND l.parentId = :businessId")
  Page<Link> findByBusinessId(@Param("businessId") Long businessId, Pageable pageable);

  @Query("SELECT l FROM Link l WHERE l.parentId = :businessId AND l.businessId = :businessId AND l.linkParentType = :linkParentType")
  List<Link> findByBusiness(@Param("businessId") Long businessId, @Param("linkParentType") LinkParentType linkParentType);

  @Query("SELECT l FROM Link l WHERE l.parentId = :chatSpaceId AND l.chatSpaceId = :chatSpaceId AND l.linkParentType = :linkParentType")
  List<Link> findByChatSpace(@Param("chatSpaceId") Long chatSpaceId, @Param("linkParentType") LinkParentType linkParentType);

  @Query(value = """
    SELECT l FROM Link l
    WHERE
      l.parentId = :businessId AND
      l.businessId = :businessId AND
      l.linkParentType = :linkParentType AND
      l.linkType IN (:linkTypes)
  """)
  List<Link> findByBusiness(@Param("businessId") Long businessId, @Param("linkParentType") LinkParentType linkParentType, @Param("linkTypes") List<LinkType> linkType);

  @Query(value = """
    SELECT l FROM Link l
    WHERE
      l.parentId = :chatSpaceId AND
      l.chatSpaceId = :chatSpaceId AND
      l.linkParentType = :linkParentType AND
      l.linkType IN (:linkTypes)
  """)
  List<Link> findByChatSpace(@Param("chatSpaceId") Long chatSpaceId, @Param("linkParentType") LinkParentType linkParentType, @Param("linkTypes") List<LinkType> linkType);
}
