package io.github.nanamochi.chio.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Permissions {
  NO_PERMISSIONS(0),
  REGULAR(1),
  BAT(1 << 1),
  SUPPORTER(1 << 2),
  FRIEND(1 << 3),
  PEPPY(1 << 4),
  TOURNAMENT(1 << 5);

  private final int value;

  public static Permissions fromValue(int value) {
    for (Permissions permission : values()) {
      if (permission.value == value) return permission;
    }
    throw new IllegalArgumentException("Unknown Permissions value: " + value);
  }
}
