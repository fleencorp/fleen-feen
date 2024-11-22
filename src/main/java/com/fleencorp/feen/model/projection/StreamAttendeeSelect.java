package com.fleencorp.feen.model.projection;

import com.fleencorp.feen.constant.stream.JoinStatus;
import com.fleencorp.feen.constant.stream.StreamAttendeeRequestToJoinStatus;
import com.fleencorp.feen.constant.stream.StreamVisibility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static java.util.Objects.nonNull;

/**
 * @see <a href="https://velog.io/@pjh612/Spring-Data-JPA%EC%97%90%EC%84%9C%EC%9D%98-Projection-%EB%B0%A9%EB%B2%95">
 *   Spring Data JPA에서의 Projection 방법</a>
 *
 * @see <a href="https://velog.io/@rnqhstlr2297/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%8D%B0%EC%9D%B4%ED%84%B0-JPA-Projections-%EB%B0%8F-Native-Query">
 *   Jpa Projects with Native Query</a>
 *
 * @see <a href="https://velog.io/@klmin/Spring-boot-Spring-data-jpa-Projection">
 *   [Spring boot] Spring data jpa Projection</a>
 *
 * @see <a href="https://velog.io/@hyeok_1212/spring-data-jpa-projections">
 *   Spring Data JPA Get only the columns you want</a>
 *
 * @see <a href="https://velog.io/@hyunho058/JPA">
 *   [JPA] Querydsl을 이용한 Projection</a>
 *
 * @see <a href="https://velog.io/@pak4184/Querydsl-dto-%EB%B0%98%ED%99%98-Projection-%EB%8F%99%EC%A0%81-%EC%BF%BC%EB%A6%AC-Bulk-Query-Sql-function-%ED%98%B8%EC%B6%9C">
 *   Querydsl - Return dto, Projection, Dynamic query, Bulk query, Sql function call</a>
 *
 * @see <a href="https://0soo.tistory.com/206">
 *   Querydsl Projection - with 1:N DTO 매핑 (프로젝션) 출처</a>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StreamAttendeeSelect {

  private Long eventOrStreamId;
  private StreamAttendeeRequestToJoinStatus requestToJoinStatus;
  private Boolean isAttending;
  private StreamVisibility streamVisibility;
  private LocalDateTime endDate;

  public boolean isAttending() {
    return nonNull(isAttending) && isAttending;
  }

  public JoinStatus getJoinStatus() {
    return JoinStatus.getJoinStatus(requestToJoinStatus, streamVisibility, endDate.isBefore(LocalDateTime.now()), isAttending);
  }

}
