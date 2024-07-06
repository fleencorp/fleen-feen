package com.fleencorp.feen.model.domain.user;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import static com.fleencorp.feen.util.security.UserAuthoritiesUtil.ROLE_PREFIX;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "role", uniqueConstraints = {
  @UniqueConstraint(columnNames = {"code"})
})
public class Role extends FleenFeenEntity {

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "role_id", nullable = false, updatable = false, unique = true)
  private Long roleId;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "code", nullable = false, length = 100)
  private String code;

  @Column(name = "description", length = 1000)
  private String description;

  public static Role of(long id) {
    return Role.builder()
            .roleId(id)
            .build();
  }

  public static Role of(String title, String code) {
    return Role.builder()
            .title(title)
            .code(code)
            .build();
  }

  public static Role of(String authority) {
    if (nonNull(authority)) {
      return Role
              .builder()
              .code(authority.replace(ROLE_PREFIX, ""))
              .build();
    }
    return null;
  }
}
