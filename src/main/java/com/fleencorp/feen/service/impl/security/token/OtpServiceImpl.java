package com.fleencorp.feen.service.impl.security.token;

import com.fleencorp.feen.exception.stream.UnableToCompleteOperationException;
import com.fleencorp.feen.service.security.OtpService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;


/**
 * Implementation of the OTP (One-Time Password) service interface.
 * This class provides methods for generating and validating OTP codes.
 * It utilizes various algorithms and utilities to handle OTP functionalities.
 *
 * 
 * @see <a href="https://creampuffy.tistory.com/89">Implementing Google Authenticator Authentication in Java</a>
 *
 * @author Yusuf Alamu Musa
 * @author Richard
 * @version 1.0
 */
@Slf4j
@Service
@Primary
public class OtpServiceImpl implements OtpService {

  /**
   * Generates a random secret key to be used for OTP (One-Time Password) generation.
   *
   * @return A randomly generated secret key encoded as a Base32 string.
   */
  public String generateSecretKey() {
    final SecureRandom random = new SecureRandom();
    final byte[] bytes = new byte[20];
    random.nextBytes(bytes);
    final Base32 base32 = new Base32();
    return base32.encodeToString(bytes);
  }


  /**
   * Validates the provided OTP code against the generated OTP code using the given secret key.
   *
   * @param code   The OTP code to be validated.
   * @param secret The secret key used to generate the OTP code.
   * @return True if the provided OTP code is valid, false otherwise.
   */
  public boolean validateOtpCode(final String code, final String secret) {
    final String generatedCode = getOtpCode(secret);
    return code.equals(generatedCode);
  }


  /** Generates an OTP code using the provided secret key.
   *
   *  @param secretKey The secret key used for OTP generation.
   *  @return The generated OTP code.
   */
  public String getOtpCode(final String secretKey) {
    // Decode the secret key from Base32
    final byte[] bytes = new Base32().decode(secretKey);
    // Generate OTP using TOTP algorithm
    return TOTP.getOTP(Hex.encodeHexString(bytes));
  }


  /** Generates an OTP authentication URL based on the provided parameters.
   *
   *  @param secretKey The secret key used for OTP generation.
   *  @param account The account for which OTP is generated.
   *  @param issuer The issuer or provider of the OTP.
   *  @return The OTP authentication URL.
   *  @throws RuntimeException if there are error encoding URL components.
   */
  public String getOtpAuthURL(final String secretKey, final String account, final String issuer) {
    try {
      // URL encode the account, secret key, and issuer
      final String encodedAccount = urlEncode(issuer + ":" + account);
      final String encodedSecret = urlEncode(secretKey);
      final String encodedIssuer = urlEncode(issuer);

      // Generate and format the OTP authentication URL
      return String.format(
        "otpauth://totp/%s?secret=%s&issuer=%s",
        encodedAccount, encodedSecret, encodedIssuer
      );
    } catch (final Exception ex) {
      log.error("Error occurred while generating OTP Auth URI. Reason: {}", ex.getMessage());
      throw new UnableToCompleteOperationException();
    }
  }


  /**
   * Encodes the provided value for URL usage.
   *
   * @param value The value to be URL encoded.
   * @return The URL encoded value.
   */
  private String urlEncode(final String value) {
    return URLEncoder.encode(value, UTF_8).replace("+", "%20");
  }


  /**
   * Generates a Data URI containing QR code image data for the provided OTP authentication URL,
   * with specified dimensions.
   *
   * @param googleOTPAuthURL The OTP authentication URL generated by Google Authenticator.
   * @param height            The height of the QR code image.
   * @param width             The width of the QR code image.
   * @return A Data URI string containing the QR code image data.
   * @throws WriterException if an error occurs while encoding the QR code.
   * @throws IOException     if an I/O error occurs.
   */
  public String getQRImageDataURI(final String googleOTPAuthURL, final int height, final int width) throws WriterException, IOException {
    final byte[] imageBytes = getQRImageBytes(googleOTPAuthURL, height, width);
    final String base64Image = Base64.getEncoder().encodeToString(imageBytes);
    return "data:image/png;base64," + base64Image;
  }


  /**
   * Generates a Data URI containing QR code image data for the provided OTP authentication URL,
   * with default dimensions of 300x300 pixels.
   *
   * @param googleOTPAuthURL The OTP authentication URL generated by Google Authenticator.
   * @return A Data URI string containing the QR code image data.
   */
  public String getQRImageDataURI(final String googleOTPAuthURL) {
    try {
      return getQRImageDataURI(googleOTPAuthURL, 300, 300);
    } catch (final WriterException | IOException ex) {
      log.error("Error has occurred. Reason: {}", ex.getMessage());
    }
    return null;
  }


  /**
   * Generates a byte array containing QR code image data for the provided OTP authentication URL,
   * with specified dimensions.
   *
   * @param googleOTPAuthURL The OTP authentication URL generated by Google Authenticator.
   * @param height            The height of the QR code image.
   * @param width             The width of the QR code image.
   * @return A byte array containing the QR code image data.
   * @throws WriterException if an error occurs while encoding the QR code.
   * @throws IOException     if an I/O error occurs.
   */
  public byte[] getQRImageBytes(final String googleOTPAuthURL, final int height, final int width) throws WriterException, IOException {
    final BitMatrix matrix = new MultiFormatWriter().encode(googleOTPAuthURL, BarcodeFormat.QR_CODE, width, height);
    try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      MatrixToImageWriter.writeToStream(matrix, "png", out);
      return out.toByteArray();
    }
  }

}