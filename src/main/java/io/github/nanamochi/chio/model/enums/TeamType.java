package io.github.nanamochi.chio.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TeamType {
  HEAD_TO_HEAD(0),
  TAG_COOP(1),
  TEAM_VS(2),
  TAG_TEAM_VS(3);

  private final int value;

  public static TeamType fromValue(int value) {
    for (TeamType t : values()) {
      if (t.value == value) return t;
    }
    throw new IllegalArgumentException("Unknown TeamType value: " + value);
  }
}
