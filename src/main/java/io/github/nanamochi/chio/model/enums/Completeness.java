package io.github.nanamochi.chio.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Completeness {
  STATUS_ONLY(0),
  STATISTICS(1),
  FULL(2);

  private final int value;

  public static Completeness fromValue(int value) {
    for (Completeness c : Completeness.values()) {
      if (c.value == value) return c;
    }
    throw new IllegalArgumentException("Unknown Completeness value: " + value);
  }
}
