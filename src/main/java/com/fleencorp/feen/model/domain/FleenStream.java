package com.fleencorp.feen.model.domain;

import com.fleencorp.feen.constant.stream.StreamType;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

import static jakarta.persistence.EnumType.STRING;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FleenStream extends FleenFeenEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false, unique = true, length = 36)
  private Long id;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "description", nullable = false, length = 3000)
  private String description;

  @Column(name = "location", nullable = false, length = 100)
  private String location;

  @Column(name = "timezone", nullable = false, length = 30)
  private String timezone;

  @Column(name = "organizer_name", nullable = false, updatable = false, length = 100)
  private String organizerName;

  @Column(name = "organizer_email", nullable = false, updatable = false, length = 100)
  private String organizerEmail;

  @Column(name = "organizer_phone", nullable = false, updatable = false, length = 100)
  private String organizerPhone;

  @Column(name = "made_for_kids", nullable = false)
  private Boolean forKids;

  @Column(name = "stream_link", nullable = false, updatable = false, length = 1000)
  private String streamLink;

  @Column(name = "thumbnail_link", nullable = false, length = 1000)
  private String thumbnailLink;

  @Column(name = "stream_typw", nullable = false)
  @Enumerated(STRING)
  private StreamType streamType;

  @Column(name = "stream_visibility", nullable = false)
  @Enumerated(STRING)
  private StreamVisibility streamVisibility;

  @Column(name = "scheduled_start_date", nullable = false)
  private LocalDateTime scheduledStartDate;

  @Column(name = "scheduled_end_date", nullable = false)
  private LocalDateTime scheduledEndDate;
}
