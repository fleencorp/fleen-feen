package com.fleencorp.feen.util.security;

import com.fleencorp.feen.exception.security.DecryptionFailedException;
import com.fleencorp.feen.exception.security.EncryptionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class for encryption and decryption operations.
 *
 * <p>This class provides static methods to perform encryption and decryption using various algorithms
 * and techniques, ensuring data security in applications that require sensitive information handling.</p>
 *
 * <p>Currently, the class supports symmetric encryption algorithms such as AES (Advanced Encryption Standard)
 * and provides methods to encrypt plaintext data and decrypt encrypted data.</p>
 *
 * <p>Note: It is crucial to handle encryption keys securely and manage them properly to maintain the integrity
 * and confidentiality of encrypted data.</p>
 *
 * @author Yusuf Alamu Musa
 * @version 1.0
 */
@Component
@Slf4j
public class EncryptionUtils {

  private final String encryptionKey;
  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final String ALGORITHM = "AES";

  public EncryptionUtils(@Value("${entity.field.encryption.key}") final String encryptionKey) {
    this.encryptionKey = encryptionKey;
  }

  /**
   * Encrypts a plaintext value using AES-GCM (Advanced Encryption Standard with Galois/Counter Mode).
   *
   * <p>This method encrypts the provided plaintext value using AES-GCM encryption algorithm,
   * which is a symmetric encryption algorithm known for its security and efficiency in protecting
   * sensitive data. The encryption process involves initializing a Cipher instance in ENCRYPT_MODE,
   * using a secret key and GCM parameters obtained from {@link #getSecretKeySpec()} and {@link #getGCMParameterSpec()}.
   * The encrypted value is then Base64-encoded for safe storage and transmission.</p>
   *
   * <p>If an error occurs during encryption, an EncryptionFailedException is thrown with an error message
   * containing details about the exception.</p>
   *
   * @param value The plaintext value to be encrypted.
   * @return The Base64-encoded encrypted value.
   * @throws EncryptionFailedException If encryption fails due to an exception.
   */
  public String encrypt(final String value) {
    try {
      final Cipher cipher = getCipher();
      cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(), getGCMParameterSpec());
      return Base64.encodeBase64String(cipher.doFinal(value.getBytes(UTF_8)));
    } catch (final Exception ex) {
      final String errorMessage = String
        .format("An error occurred while calling encrypt of %s. Reason: %s",
          ex.getClass().getName(),
          ex.getMessage());
        log.error(errorMessage);
      throw new EncryptionFailedException(errorMessage);
    }
  }

  /**
   * Decrypts an encrypted value using AES-GCM (Advanced Encryption Standard with Galois/Counter Mode).
   *
   * <p>This method decrypts the provided encrypted value using AES-GCM decryption algorithm,
   * which is a symmetric encryption algorithm known for its security and efficiency in decrypting
   * encrypted data. The decryption process involves initializing a Cipher instance in DECRYPT_MODE,
   * using a secret key and GCM parameters obtained from {@link #getSecretKeySpec()} and {@link #getGCMParameterSpec()}.
   * The encrypted value is expected to be Base64-encoded before decryption.</p>
   *
   * <p>If an error occurs during decryption, a DecryptionFailedException is thrown with an error message
   * containing details about the exception.</p>
   *
   * @param encryptedValue The Base64-encoded encrypted value to be decrypted.
   * @return The decrypted plaintext value.
   * @throws DecryptionFailedException If decryption fails due to an exception.
   */
  public String decrypt(final String encryptedValue) {
    try {
      final Cipher cipher = getCipher();
      cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(), getGCMParameterSpec());
      return new String(cipher.doFinal(Base64.decodeBase64(encryptedValue)), UTF_8);
    } catch (final Exception ex) {
      final String errorMessage = String
        .format("An error occurred while calling decrypt of %s. Reason: %s",
          ex.getClass().getName(),
          ex.getMessage());
        log.error(errorMessage);
      throw new DecryptionFailedException(errorMessage);
    }
  }

  /**
   * Retrieves a Cipher instance for encryption and decryption operations.
   *
   * <p>This method returns a Cipher instance configured with the specified transformation,
   * which defines the encryption algorithm, mode of operation, and padding scheme. It is used
   * for performing encryption and decryption operations on data.</p>
   *
   * <p>The Cipher instance is obtained using the {@link Cipher#getInstance(String)} method,
   * which may throw NoSuchPaddingException or NoSuchAlgorithmException if the specified
   * transformation is not available on the system.</p>
   *
   * @return A Cipher instance configured with the specified transformation.
   * @throws NoSuchPaddingException If the specified padding scheme is not available.
   * @throws NoSuchAlgorithmException If the specified encryption algorithm is not available.
   */
  private Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
    return Cipher.getInstance(EncryptionUtils.TRANSFORMATION);
  }

  /**
   * Generates a SecretKeySpec for AES encryption.
   *
   * <p>This method creates and returns a SecretKeySpec object, which encapsulates the secret key
   * used for AES (Advanced Encryption Standard) encryption. The SecretKeySpec is initialized with
   * the encryption key converted to bytes using UTF-8 encoding and the specified encryption algorithm.</p>
   *
   * <p>The encryption key should be securely generated and managed to ensure the security and integrity
   * of encrypted data.</p>
   *
   * @return A SecretKeySpec object initialized with the encryption key and algorithm.
   */
  private SecretKeySpec getSecretKeySpec() {
    return new SecretKeySpec(encryptionKey.getBytes(UTF_8), EncryptionUtils.ALGORITHM);
  }

  /**
   * Generates a GCM (Galois/Counter Mode) parameter specification for AES encryption.
   *
   * <p>This method creates and returns a GCMParameterSpec object, which specifies the parameters
   * required for AES-GCM (Advanced Encryption Standard with Galois/Counter Mode) encryption. The GCM
   * mode is chosen for its cryptographic security and efficient performance in symmetric encryption.</p>
   *
   * <p>The GCMParameterSpec specifies a 128-bit authentication tag length and uses the encryption key
   * converted to bytes using UTF-8 encoding as the initialization vector (IV) for the encryption process.</p>
   *
   * @return A GCMParameterSpec object configured with 128-bit tag length and the encryption key as IV.
   */
  private GCMParameterSpec getGCMParameterSpec() {
    return new GCMParameterSpec(128, encryptionKey.getBytes(UTF_8));
  }
}
