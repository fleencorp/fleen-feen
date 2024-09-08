package com.fleencorp.feen.model.dto.auth;

import com.fleencorp.feen.model.dto.verification.ResendVerificationCodeDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@NoArgsConstructor
public class ResendSignUpVerificationCodeDto extends ResendVerificationCodeDto {

}
