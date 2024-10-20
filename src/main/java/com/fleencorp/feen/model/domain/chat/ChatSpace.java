package com.fleencorp.feen.model.domain.chat;

import com.fleencorp.base.converter.impl.security.StringCryptoConverter;
import com.fleencorp.feen.constant.chat.space.ChatSpaceVisibility;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.EAGER;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chat_space")
public class ChatSpace extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "chat_space_id", nullable = false, updatable = false, unique = true)
  private Long chatSpaceId;

  @Column(name = "external_id_or_name", length = 1000)
  private String externalIdOrName;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "description", nullable = false, length = 3000)
  private String description;

  @Column(name = "tags", length = 300)
  private String tags;

  @Column(name = "guidelines_or_rules", nullable = false, length = 3000)
  private String guidelinesOrRules;

  @Column(name = "space_link", length = 1000)
  @Convert(converter = StringCryptoConverter.class)
  private String spaceLink;

  @Column(name = "member_id", insertable = false, updatable = false)
  private Long memberId;

  @CreatedBy
  @ManyToOne(fetch = EAGER, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Enumerated(STRING)
  @Column(name = "space_visibility", nullable = false)
  private ChatSpaceVisibility spaceVisibility;

  @Builder.Default
  @Column(name = "is_active", nullable = false)
  private Boolean isActive = true;

  @Builder.Default
  @Column(name = "total_members", nullable = false)
  private Long totalMembers = 0L;

  @Builder.Default
  @Column(name = "is_deleted", nullable = false)
  private Boolean isDeleted = false;

  @Builder.Default
  @OneToMany(fetch = LAZY, mappedBy = "chatSpace", targetEntity = ChatSpaceMember.class, cascade = CascadeType.PERSIST)
  private Set<ChatSpaceMember> members = new HashSet<>();

  /**
   * Retrieves the member ID associated with this member.
   *
   * @return the member ID if the member is not null; otherwise, returns null.
   */
  public Long getMemberId() {
    return nonNull(member) ? member.getMemberId() : null;
  }

  /**
   * Updates the details of this member with the specified external ID or name
   * and the space link.
   *
   * @param externalIdOrName the external ID or name to set for this member.
   * @param spaceLink the link to the space associated with this member.
   */
  public void updateDetails(final String externalIdOrName, final String spaceLink) {
    this.externalIdOrName = externalIdOrName;
    this.spaceLink = spaceLink;
  }

  /**
   * Updates the details of the event or stream, including the title, description, tags, and guidelines or rules.
   * This method allows modification of the provided details that describe and categorize the event or stream.
   *
   * @param title the new title to be set for the event or stream.
   * @param description the updated description providing information about the event or stream.
   * @param tags the updated tags for categorizing or filtering the event or stream.
   * @param guidelinesOrRules the new guidelines or rules associated with the event or stream.
   * @param spaceVisibility the new visibility whether to be private or public
   */
  public void updateDetails(final String title, final String description, final String tags, final String guidelinesOrRules, final ChatSpaceVisibility spaceVisibility) {
    this.title = title;
    this.description = description;
    this.tags = tags;
    this.guidelinesOrRules = guidelinesOrRules;
    this.spaceVisibility = spaceVisibility;
  }

  /**
   * Disables this member, setting their active status to false.
   * This method is typically called to deactivate a member from a chat space
   * or a service.
   */
  public void disable() {
    isActive = false;
  }

  /**
   * Enables this member, setting their active status to true.
   * This method is typically called to reactivate a member who was previously
   * disabled.
   */
  public void enable() {
    isActive = true;
  }

  /**
   * Marks this member as deleted, setting their deletion status to true.
   * This method indicates that the member is no longer active or relevant
   * within the context of the application.
   */
  public void delete() {
    isDeleted = true;
  }

  /**
   * Checks if this member is marked as deleted.
   *
   * @return true if the member is deleted; otherwise, returns false.
   */
  public boolean isDeleted() {
    return isDeleted;
  }

  /**
   * Checks if this member is inactive.
   *
   * @return true if the member is inactive; otherwise, returns false.
   */
  public boolean isInactive() {
    return !isActive;
  }

  /**
   * Checks if the chat space is private.
   *
   * <p>This method determines if the visibility of the chat space is set to
   * private by delegating the check to the {@link ChatSpaceVisibility} enum.</p>
   *
   * @return true if the chat space visibility is private; otherwise, returns false.
   */
  public boolean isPrivate() {
    return ChatSpaceVisibility.isPrivate(spaceVisibility);
  }

  /**
   * Increments the total number of members in the chat space by one.
   */
  public void increaseTotalMembers() {
    totalMembers++;
  }

  /**
   * Decrements the total number of members in the chat space by one.
   */
  public void decreaseTotalMembers() {
    totalMembers--;
  }

  /**
   * Retrieves the organizer's full name.
   *
   * @return The full name of the organizer if the member is not null; {@code null} otherwise.
   */
  public String getOrganizerName() {
    return nonNull(member) ? member.getFullName() : null;
  }

  /**
   * Retrieves the organizer's email address.
   *
   * @return The email address of the organizer if the member is not null; {@code null} otherwise.
   */
  public String getOrganizerEmail() {
    return nonNull(member) ? member.getEmailAddress() : null;
  }

  /**
   * Retrieves the organizer's phone number.
   *
   * @return The phone number of the organizer if the member is not null; {@code null} otherwise.
   */
  public String getOrganizerPhone() {
    return nonNull(member) ? member.getPhoneNumber() : null;
  }

  public static ChatSpace of(final Long chatSpaceId) {
    return ChatSpace.builder()
      .chatSpaceId(chatSpaceId)
      .build();
  }
}
