package com.fleencorp.feen.service.security;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

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
    final Random obj = new Random();
    final char[] otp = new char[length];

    for (int i = 0; i < 6; i++)  {
      otp[i]= (char) (obj.nextInt(10) + 48);
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

  static String getRandomSixDigitOtp() {
    return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
  }

  String generateSecretKey();

  boolean validateOtpCode(String code, String secret);

  String getQRImageDataURI(String authUri);

  String getOtpAuthURL(String secretKey, String secretLabel, String secretIssuer);
}
