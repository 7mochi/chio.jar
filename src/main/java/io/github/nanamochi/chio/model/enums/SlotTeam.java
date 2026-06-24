package io.github.nanamochi.chio.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlotTeam {
  NEUTRAL(0),
  BLUE(1),
  RED(2);

  private final int value;

  public SlotTeam getOpposite() {
    if (this == RED) return BLUE;
    if (this == BLUE) return RED;
    return NEUTRAL;
  }

  public static SlotTeam fromValue(int value) {
    for (SlotTeam team : values()) {
      if (team.value == value) return team;
    }
    throw new IllegalArgumentException("Unknown SlotTeam value: " + value);
  }
}
