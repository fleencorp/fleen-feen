package com.fleencorp.feen.user.validator.impl;

import com.fleencorp.feen.user.service.member.MemberService;
import com.fleencorp.feen.user.validator.MemberExist;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import static java.util.Objects.nonNull;

/**
 * Validator for checking the existence of a member.
 *
 * <p>This class implements the {@link ConstraintValidator} interface and is used to validate
 * whether a member with the given ID exists in the system.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
public class MemberExistValidator implements ConstraintValidator<MemberExist, String> {

  private final MemberService service;

  /**
   * Constructs a {@code MemberExistValidator} with the specified {@code MemberService}.
   *
   * @param service the {@link MemberService} used to check the existence of a member.
   */
  public MemberExistValidator(final MemberService service) {
    this.service = service;
  }

  /**
   * Validates whether the given member ID exists.
   *
   * @param memberId The member ID to validate, provided as a string.
   * @param context The context in which the constraint is evaluated.
   * @return {@code true} if the member ID is valid (exists or is null); {@code false} otherwise.
   */
  @Override
  public boolean isValid(final String memberId, final ConstraintValidatorContext context) {
    if (nonNull(memberId)) {
      return service.isIdExists(Long.parseLong(memberId));
    }
    return true;
  }
}
