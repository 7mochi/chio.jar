package io.github.nanamochi.chio.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScoringType {
  SCORE(0),
  ACCURACY(1),
  COMBO(2),
  SCORE_V2(3);

  private final int value;

  public static ScoringType fromValue(int value) {
    for (ScoringType t : values()) {
      if (t.value == value) return t;
    }
    throw new IllegalArgumentException("Unknown ScoringType value: " + value);
  }
}
