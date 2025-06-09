package com.fleencorp.feen.user.model.domain;

import com.fleencorp.feen.model.domain.base.FleenFeenEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.fleencorp.feen.user.util.UserAuthoritiesUtil.ROLE_PREFIX;
import static jakarta.persistence.GenerationType.IDENTITY;
import static java.util.Objects.nonNull;

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

  public static Role of(final long id) {
    final Role role = new Role();
    role.setRoleId(id);

    return role;
  }

  public static Role of(final String title, final String code) {
    final Role role = new Role();
    role.setTitle(title);
    role.setCode(code);

    return role;
  }

  public static Role of(final String authority) {
    if (nonNull(authority)) {
      final Role role = new Role();
      role.setCode(authority.replace(ROLE_PREFIX, ""));
      return role;
    }
    return null;
  }
}
