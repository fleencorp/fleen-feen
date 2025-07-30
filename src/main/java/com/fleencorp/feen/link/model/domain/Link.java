package com.fleencorp.feen.link.model.domain;

import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "link")
public class Link {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "link_id", nullable = false, updatable = false, unique = true)
  private Long linkId;

  @Column(name = "parent_id", updatable = false)
  private Long parentId;

  @Enumerated(STRING)
  @Column(name = "link_parent_type", nullable = false)
  private LinkParentType linkParentType;

  @Enumerated(EnumType.STRING)
  @Column(name = "link_type", nullable = false)
  private LinkType linkType;

  @Column(name = "url", nullable = false, length = 1000)
  private String url;

  @Column(name = "chat_space_id", updatable = false, insertable = false)
  private Long chatSpaceId;

  @ManyToOne(fetch = LAZY, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", updatable = false)
  private ChatSpace chatSpace;

}
