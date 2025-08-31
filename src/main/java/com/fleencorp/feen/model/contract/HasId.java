package com.fleencorp.feen.model.contract;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

public interface HasId {

  Long getNumberId();

  static <T extends HasId> List<Long> getIdsContainIn(final Collection<T> entries, final Map<Long, ?> map) {
    return entries.stream()
      .map(HasId::getNumberId)
      .filter(map::containsKey)
      .toList();
  }

  static <T extends HasId> List<Long> getIdsNotContainIn(final Collection<T> entries, final Map<Long, ?> map) {
    return entries.stream()
      .map(HasId::getNumberId)
      .filter(id -> !map.containsKey(id))
      .toList();
  }

  static <T extends HasId> List<Long> getIds(final Collection<T> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .map(HasId::getNumberId)
        .toList();
    }

    return List.of();
  }

  /**
   * Groups a collection of items implementing {@link HasId} by their numeric ID.
   *
   * <p>This is typically used to map stream IDs to their corresponding attendance
   * or membership objects. Null entries are filtered out. Returns an empty map
   * if the input collection is null or empty.</p>
   *
   * @param entries the collection of items to group
   * @param <T> a type that implements {@link HasId}
   * @return a map of numeric IDs to their corresponding items
   */
  static <T extends HasId> Map<Long, T> groupMembershipByEntriesId(final Collection<T> entries) {
    if (nonNull(entries) && !entries.isEmpty()) {
      return entries.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toMap(HasId::getNumberId, Function.identity()));
    }
    return Map.of();
  }

}
