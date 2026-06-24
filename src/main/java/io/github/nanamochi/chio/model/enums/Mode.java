package io.github.nanamochi.chio.model.enums;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mode {
  OSU(0, "osu!", "osu"),
  TAIKO(1, "Taiko", "taiko"),
  CATCH(2, "CatchTheBeat", "fruits"),
  MANIA(3, "osu!mania", "mania");

  private final int value;
  private final String formatted;
  private final String alias;

  private static final Map<String, Mode> aliasMap = new HashMap<>();

  static {
    for (Mode m : values()) aliasMap.put(m.alias, m);
  }

  public static Mode fromAlias(String input) {
    return aliasMap.get(input);
  }

  public static Mode fromValue(int value) {
    for (Mode m : values()) {
      if (m.value == value) return m;
    }
    throw new IllegalArgumentException("Unknown Mode value: " + value);
  }
}
