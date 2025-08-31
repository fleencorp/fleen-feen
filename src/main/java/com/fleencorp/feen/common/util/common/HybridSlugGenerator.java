package com.fleencorp.feen.common.util.common;

import java.text.Normalizer;
import java.util.UUID;
import java.util.regex.Pattern;

public final class HybridSlugGenerator {

  private HybridSlugGenerator() {}

  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9-]");
  private static final Pattern HYPHENS = Pattern.compile("-+");

  /**
   * Generates a hybrid slug from the given title by combining a sanitized title
   * segment with a random UUID fragment.
   *
   * <p>The method normalizes and sanitizes the input title by removing diacritical
   * marks, converting it to lowercase, replacing whitespace with hyphens, and
   * stripping non-alphanumeric characters (except hyphens). The sanitized title
   * is truncated to a maximum of 17 characters to ensure that the final slug fits
   * within a 30-character budget. A random UUID is generated, and its first 12
   * hexadecimal characters are appended to the sanitized title, separated by a hyphen.
   * If the sanitized title is empty, only the UUID fragment is returned.</p>
   *
   * @param title the input title to be transformed into a slug
   * @return a slug composed of a sanitized title segment and a UUID fragment
   */
  public static String generateHybridSlug(final String title) {
    final UUID uuid = UUID.randomUUID();

    // Sanitize and lowercase the title
    String sanitizedTitle = Normalizer.normalize(title, Normalizer.Form.NFD)
      .replaceAll("\\p{M}", "")
      .toLowerCase()
      .replaceAll("\\s+", "-");

    // Remove non-alphanumeric characters except hyphens
    sanitizedTitle = NON_ALPHANUMERIC.matcher(sanitizedTitle).replaceAll("");

    // Truncate to fit 30-char budget (e.g., 17 chars + "-" + 12 chars)
    if (sanitizedTitle.length() > 17) {
      sanitizedTitle = sanitizedTitle.substring(0, 17);
    }

    // Clean up hyphens
    sanitizedTitle = HYPHENS.matcher(sanitizedTitle).replaceAll("-");
    sanitizedTitle = sanitizedTitle.replaceAll("^-|-$", "");

    // Use 12 hex chars (48 bits randomness = 281 trillion possibilities)
    final String uuidPart = uuid.toString().replace("-", "").substring(0, 12);

    // Combine
    if (sanitizedTitle.isEmpty()) {
      return uuidPart;
    }

    return sanitizedTitle + "-" + uuidPart;
  }
}

