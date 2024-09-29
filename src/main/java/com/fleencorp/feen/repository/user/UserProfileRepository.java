package com.fleencorp.feen.repository.user;

import com.fleencorp.feen.constant.security.profile.ProfileStatus;
import com.fleencorp.feen.model.domain.user.Member;
import com.fleencorp.feen.model.projection.MemberInfoSelect;
import com.fleencorp.feen.model.projection.MemberProfileStatusSelect;
import com.fleencorp.feen.model.projection.MemberUpdateSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<Member, Long> {

  @Modifying
  @Transactional
  @Query("UPDATE Member m SET m.profileStatus = :profileStatus  WHERE m = :member")
  void updateProfileStatus(@Param("member") Member member, @Param("profileStatus") ProfileStatus profileStatus);

  @Query("SELECT new com.fleencorp.feen.model.projection.MemberInfoSelect(m.memberId, m.firstName, m.lastName, m.country) FROM Member m WHERE m = :member")
  Optional<MemberInfoSelect> findInfoByMember(@Param("member") Member member);

  @Query("SELECT new com.fleencorp.feen.model.projection.MemberUpdateSelect(m.memberId, m.firstName, m.lastName, m.country) FROM Member m WHERE m = :member")
  Optional<MemberUpdateSelect> findByMember(@Param("member") Member member);

  @Query("SELECT new com.fleencorp.feen.model.projection.MemberProfileStatusSelect(m.profileStatus) FROM Member m WHERE m = :member")
  Optional<MemberProfileStatusSelect> findStatusByMember(@Param("member") Member member);

  @Modifying
  @Query("UPDATE Member m SET m.password = :password WHERE m = :member")
  void updatePassword(@Param("member") Member member, String password);
}
