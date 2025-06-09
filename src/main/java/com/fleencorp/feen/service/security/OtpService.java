package com.fleencorp.feen.service.security;

import static com.fleencorp.feen.user.util.TokenUtil.SECURE_RANDOM;

/**
 * Service interface for generating and validating OTPs (One-Time Passwords).
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
public interface OtpService {

  /**
   * Generates a random OTP (One-Time Password) of specified length.
   *
   * <p>The OTP consists of numeric characters only.</p>
   *
   * @param length Length of the OTP to generate.
   * @return Randomly generated OTP as a string of numeric characters.
   */
  static String generateOtp(final int length) {
    final char[] otp = new char[length];

    for (int i = 0; i < 6; i++)  {
      otp[i]= (char) (SECURE_RANDOM.nextInt(10) + 48);
    }
    return String.valueOf(otp);
  }

  /**
   * Generates a random OTP (One-Time Password) of default length 6.
   *
   * <p>The OTP consists of numeric characters only.</p>
   *
   * @return Randomly generated OTP as a string of 6 numeric characters.
   */
  static String generateOtp() {
    return generateOtp(6);
  }

  /**
   * Generates a random six-digit OTP (One-Time Password).
   *
   * <p>This method uses a cryptographically secure random number generator to
   * ensure that the generated OTP is sufficiently unpredictable.
   * The OTP is always a six-digit number between 100000 and 999999 inclusive.</p>
   *
   * @return a randomly generated six-digit OTP as a string
   */
  static String getRandomSixDigitOtp() {
    final int otp = SECURE_RANDOM.nextInt(900000) + 100000;
    return String.valueOf(otp);
  }

  String generateSecretKey();

  boolean validateOtpCode(String code, String secret);

  String getQRImageDataURI(String authUri);

  String getOtpAuthURL(String secretKey, String secretLabel, String secretIssuer);
}
