package com.fleencorp.feen.constant.external.google.calendar.calendar;

import com.fleencorp.base.constant.base.ApiParameter;
import lombok.Getter;

import static com.fleencorp.base.util.EnumUtil.parseEnumOrNull;

/**
* Enum representing types of ACL scopes.
*
* @author Yusuf Alamu Musa
* @version 1.0
*/
@Getter
public enum AclScopeType implements ApiParameter {

  /**
   * Represents the default or public scope. This scope type is used to allow access to anyone
   * who has the link to the resource or when the resource is set to be publicly available.
   * It typically grants public or anonymous access.
   */
  DEFAULT("default"),

  /**
   * Represents the user scope. This scope type is used to grant permissions to a specific user
   * identified by an email address. It allows for fine-grained control by granting access
   * to individual users.
   * Example: Sharing with a specific person's email like user@example.com.
   */
  USER("user"),


  /**
   * Represents the group scope. This scope type is used to share resources with a Google Group.
   * The group is identified by a group email address, and permissions are applied to all members
   * of that group.
   * Example: Sharing with a team using a group email like team@googlegroups.com.
   */
  GROUP("group"),

  /**
   * Represents the domain scope. This scope type is used to grant access to all users within a specific domain.
   * It is useful for sharing resources across an entire organization.
   * Example: Allowing all users within the domain example.com to access the resource.
   */
  DOMAIN("domain");

  private final String value;

  AclScopeType(final String value) {
    this.value = value;
  }

  public static AclScopeType of(final String value) {
    return parseEnumOrNull(value, AclScopeType.class);
  }
}
