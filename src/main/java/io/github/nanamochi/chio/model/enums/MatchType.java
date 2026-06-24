package io.github.nanamochi.chio.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchType {
  STANDARD(0),
  POWERPLAY(1);

  private final int value;

  public static MatchType fromValue(int value) {
    for (MatchType t : values()) {
      if (t.value == value) return t;
    }
    throw new IllegalArgumentException("Unknown MatchType value: " + value);
  }
}
