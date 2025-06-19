package com.fleencorp.feen.role.repository;

import com.fleencorp.feen.role.model.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Integer> {

  @Query(value = "SELECT DISTINCT r FROM Role r WHERE r.code IN (:codes)")
  List<Role> findRolesByCode(@Param("codes") Set<String> codes);
}
