package com.fleencorp.feen.model.domain.stream;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import com.fleencorp.feen.model.domain.user.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stream_speaker")
public class StreamSpeaker extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "stream_speaker_id", nullable = false, updatable = false, unique = true)
  private Long streamSpeakerId;

  @ManyToOne(fetch = LAZY, optional = false, targetEntity = FleenStream.class)
  @JoinColumn(name = "fleen_stream_id", referencedColumnName = "fleen_stream_id", nullable = false, updatable = false)
  private FleenStream fleenStream;

  @ManyToOne(fetch = LAZY, targetEntity = Member.class)
  @JoinColumn(name = "member_id", referencedColumnName = "member_id")
  private Member member;

  @Column(name = "full_name", nullable = false)
  private String fullName;

  @Column(name = "title", length = 100)
  private String title;

  @Column(name = "description", length = 1000)
  private String description;
}
