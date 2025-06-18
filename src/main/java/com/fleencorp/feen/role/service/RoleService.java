package com.fleencorp.feen.role.service;

import com.fleencorp.feen.role.model.domain.Role;

import java.util.List;
import java.util.Set;

public interface RoleService {

  List<Role> findAllByCode(Set<String> codes);

  List<Role> getRolesForNewUser();
}
