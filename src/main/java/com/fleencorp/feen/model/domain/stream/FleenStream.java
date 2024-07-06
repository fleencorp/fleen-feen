package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.constant.stream.StreamCreationType;
import com.fleencorp.feen.constant.stream.StreamStatus;
import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.converter.impl.StringCryptoConverter;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "fleen_stream")
public class FleenStream extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "fleen_stream_id", nullable = false, updatable = false, unique = true)
  private Long fleenStreamId;

  @Column(name = "external_id", nullable = false, updatable = false)
  private String externalId;

  @Column(name = "title", nullable = false, length = 500)
  private String title;

  @Column(name = "description", nullable = false, length = 3000)
  private String description;

  @Column(name = "tags", length = 300)
  private String tags;

  @Column(name = "location", nullable = false, length = 100)
  private String location;

  @Column(name = "timezone", nullable = false, length = 30)
  private String timezone;

  @Column(name = "organizer_name", nullable = false, updatable = false, length = 100)
  private String organizerName;

  @Column(name = "organizer_email", nullable = false, updatable = false, length = 50)
  private String organizerEmail;

  @Column(name = "organizer_phone", nullable = false, updatable = false, length = 20)
  private String organizerPhone;

  @Default
  @Column(name = "made_for_kids", nullable = false)
  private Boolean forKids = false;

  @Column(name = "stream_link", nullable = false, updatable = false, length = 1000)
  @Convert(converter = StringCryptoConverter.class)
  private String streamLink;

  @Column(name = "thumbnail_link", length = 1000)
  private String thumbnailLink;

  @Enumerated(STRING)
  @Column(name = "stream_type", nullable = false)
  private StreamType streamType;

  @Enumerated(STRING)
  @Column(name = "stream_creation_type", nullable = false)
  private StreamCreationType streamCreationType;

  @Enumerated(STRING)
  @Column(name = "stream_visibility", nullable = false)
  private StreamVisibility streamVisibility;

  @Enumerated(STRING)
  @Column(name = "stream_status", nullable = false)
  private StreamStatus streamStatus;

  @Column(name = "scheduled_start_date", nullable = false)
  private LocalDateTime scheduledStartDate;

  @Column(name = "scheduled_end_date", nullable = false)
  private LocalDateTime scheduledEndDate;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false, updatable = false)
  private Member member;

  @Default
  @OneToMany(fetch = LAZY, cascade = ALL, targetEntity = StreamAttendee.class, mappedBy = "fleenStream")
  private Set<StreamAttendee> attendees = new HashSet<>();

  public Set<StreamAttendee> getAttendees() {
    return nonNull(attendees) ? attendees : new HashSet<>();
  }

  public void update(String title, String description, String tags, String location) {
    this.title = title;
    this.description = description;
    this.tags = tags;
    this.location = location;
  }

  public void updateDetails(String externalId, String streamLink, String organizerName, String organizerEmail, String organizerPhone) {
    this.externalId = externalId;
    this.streamLink = streamLink;
    this.organizerName = organizerName;
    this.organizerEmail = organizerEmail;
    this.organizerPhone = organizerPhone;
  }

  public void updateSchedule(LocalDateTime scheduledStartDate, LocalDateTime scheduledEndDate, String timezone) {
    this.scheduledStartDate = scheduledStartDate;
    this.scheduledEndDate = scheduledEndDate;
    this.timezone = timezone;
  }
}
