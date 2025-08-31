package com.fleencorp.feen.link.model.domain;

import com.fleencorp.feen.business.model.domain.Business;
import com.fleencorp.feen.chat.space.model.domain.ChatSpace;
import com.fleencorp.feen.link.constant.LinkParentType;
import com.fleencorp.feen.link.constant.LinkType;
import com.fleencorp.feen.user.model.domain.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;

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

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = ChatSpace.class)
  @JoinColumn(name = "chat_space_id", referencedColumnName = "chat_space_id", updatable = false)
  private ChatSpace chatSpace;

  @Column(name = "business_id", updatable = false, insertable = false)
  private Long businessId;

  @ToString.Exclude
  @ManyToOne(fetch = LAZY, targetEntity = Business.class)
  @JoinColumn(name = "business_id", referencedColumnName = "business_id", updatable = false)
  private Business business;

  @Column(name = "member_id", updatable = false, insertable = false)
  private Long memberId;

  @ToString.Exclude
  @CreatedBy
  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

}
