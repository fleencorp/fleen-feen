package com.fleencorp.feen.user.repository;

import com.fleencorp.feen.user.constant.profile.ProfileStatus;
import com.fleencorp.feen.user.model.domain.Member;
import com.fleencorp.feen.user.model.projection.MemberInfoSelect;
import com.fleencorp.feen.user.model.projection.MemberProfileStatusSelect;
import com.fleencorp.feen.user.model.projection.MemberUpdateSelect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<Member, Long> {

  @Modifying
  @Query("UPDATE Member m SET m.profileStatus = :profileStatus  WHERE m = :member")
  void updateProfileStatus(@Param("member") Member member, @Param("profileStatus") ProfileStatus profileStatus);

  @Query(value = """
    SELECT new com.fleencorp.feen.user.model.projection.MemberInfoSelect(
      m.memberId,
      m.firstName,
      m.lastName,
      m.profilePhotoUrl,
      m.country
    )
    FROM Member m
    WHERE m = :member
  """)
  Optional<MemberInfoSelect> findInfoByMember(@Param("member") Member member);

  @Query(value = """
    SELECT new com.fleencorp.feen.user.model.projection.MemberUpdateSelect(
      m.memberId,
      m.firstName,
      m.lastName,
      m.emailAddress,
      m.phoneNumber,
      m.country
    )
    FROM Member m
    WHERE m = :member
  """)
  Optional<MemberUpdateSelect> findByMember(@Param("member") Member member);

  @Query(value = """
    SELECT new com.fleencorp.feen.user.model.projection.MemberProfileStatusSelect(
      m.profileStatus
    )
    FROM Member m
    WHERE m = :member
  """)
  Optional<MemberProfileStatusSelect> findStatusByMember(@Param("member") Member member);

  @Modifying
  @Query("UPDATE Member m SET m.password = :password WHERE m = :member")
  void updatePassword(@Param("member") Member member, String password);
}
