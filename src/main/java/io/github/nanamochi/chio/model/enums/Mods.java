package io.github.nanamochi.chio.model.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mods {
  NO_MOD(0, "No Mod", "NM"),
  NO_FAIL(1, "No Fail", "NF"),
  EASY(1 << 1, "Easy", "EZ"),
  NO_VIDEO(1 << 2, "No Video", "NV"), // replaced by "Touchscreen" in later versions
  HIDDEN(1 << 3, "Hidden", "HD"),
  HARD_ROCK(1 << 4, "Hard Rock", "HR"),
  SUDDEN_DEATH(1 << 5, "Sudden Death", "SD"),
  DOUBLE_TIME(1 << 6, "Double Time", "DT"),
  RELAX(1 << 7, "Relax", "RX"),
  HALF_TIME(1 << 8, "Half Time", "HT"),
  NIGHTCORE(1 << 9, "Nightcore", "NC"), // used as "Taiko" mod in older versions
  FLASHLIGHT(1 << 10, "Flashlight", "FL"),
  AUTOPLAY(1 << 11, "Autoplay", "AU"),
  SPUN_OUT(1 << 12, "Spun Out", "SO"),
  AUTOPILOT(1 << 13, "Autopilot", "AP"),
  PERFECT(1 << 14, "Perfect", "PF"),
  KEY4(1 << 15, "Key 4", "K4"),
  KEY5(1 << 16, "Key 5", "K5"),
  KEY6(1 << 17, "Key 6", "K6"),
  KEY7(1 << 18, "Key 7", "K7"),
  KEY8(1 << 19, "Key 8", "K8"),
  FADE_IN(1 << 20, "Fade In", "FI"),
  RANDOM(1 << 21, "Random", "RN"),
  CINEMA(1 << 22, "Cinema", "CN"),
  TARGET(1 << 23, "Target", "TP"),
  KEY9(1 << 24, "Key 9", "K9"),
  KEY_COOP(1 << 25, "Key Coop", "KC"),
  KEY1(1 << 26, "Key 1", "K1"),
  KEY3(1 << 27, "Key 3", "K3"),
  KEY2(1 << 28, "Key 2", "K2"),
  SCORE_V2(1 << 29, "Score V2", "V2"),
  MIRROR(1 << 30, "Mirror", "MR");

  private final int value;
  private final String displayName;
  private final String initial;

  private static final Map<String, Mods> initialMap = new HashMap<>();

  static {
    for (Mods m : values()) initialMap.put(m.initial.toUpperCase(), m);
  }

  public static List<Mods> fromBitmask(int bitmask) {
    List<Mods> result = new ArrayList<>();
    for (Mods m : values()) {
      if (m != NO_MOD && (bitmask & m.value) != 0) result.add(m);
    }
    return result;
  }

  public static List<Mods> fromInitials(String[] initials) {
    List<Mods> result = new ArrayList<>();
    for (String s : initials) {
      Mods m = initialMap.get(s.toUpperCase());
      if (m != null) result.add(m);
    }
    return result;
  }

  public static int toBitmask(List<Mods> mods) {
    int bitmask = 0;
    for (Mods m : mods) bitmask |= m.value;
    return bitmask;
  }
}
