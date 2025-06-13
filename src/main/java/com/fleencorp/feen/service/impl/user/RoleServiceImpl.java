package com.fleencorp.feen.service.impl.user;

import com.fleencorp.feen.repository.user.RoleRepository;
import com.fleencorp.feen.service.user.RoleService;
import com.fleencorp.feen.user.constant.role.RoleType;
import com.fleencorp.feen.user.model.domain.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Implementation of the {@link RoleService} interface.
 *
 * <p>This service handles operations related to roles in the application. It uses
 * the {@link RoleRepository} to interact with the data source.</p>
 *
 * <p>This implementation provides methods to manage and retrieve roles based on
 * specific criteria.</p>
 */
@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

  private final RoleRepository roleRepository;

  /**
   * Constructs a new {@link RoleServiceImpl} with the specified {@link RoleRepository}.
   *
   * @param roleRepository The repository used to perform role-related operations.
   */
  public RoleServiceImpl(final RoleRepository roleRepository) {
    this.roleRepository = roleRepository;
  }

  /**
   * Finds all roles matching the provided list of codes.
   *
   * @param codes The list of role codes to search for.
   * @return A list of {@link Role} objects matching the provided codes.
   */
  @Override
  public List<Role> findAllByCode(final Set<String> codes) {
    return roleRepository.findRolesByCode(codes);
  }

  @Override
  public List<Role> getRolesForNewUser() {
    return roleRepository.findRolesByCode(Set.of(RoleType.USER.name()));
  }
}
