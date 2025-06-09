package com.fleencorp.feen.service.user;

import com.fleencorp.feen.user.model.domain.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

  List<Role> findAllByCode(Set<String> codes);

  List<Role> getRolesForNewUser();
}
