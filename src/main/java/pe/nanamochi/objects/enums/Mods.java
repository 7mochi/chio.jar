package pe.nanamochi.objects.enums;

import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mods {
  NO_MOD(0, "No Mod", "NM"),
  NO_FAIL(1, "No Fail", "NF"),
  EASY(1 << 1, "Easy", "EZ"),
  NO_VIDEO(1 << 2, "No Video", "NV"), // Replaced by "Toushscreen" in later versions
  HIDDEN(1 << 3, "Hidden", "HD"),
  HARD_ROCK(1 << 4, "Hard Rock", "HR"),
  SUDDEN_DEATH(1 << 5, "Sudden Death", "SD"),
  DOUBLE_TIME(1 << 6, "Double Time", "DT"),
  RELAX(1 << 7, "Relax", "RX"),
  HALF_TIME(1 << 8, "Half Time", "HT"),
  NIGHTCORE(1 << 9, "Nightcore", "NC"), // Used as "Taiko" mod in older versions
  FLASHLIGHT(1 << 10, "Flashlight", "FL"),
  AUTOPLAY(1 << 11, "Autoplay", "AU"),
  SPUN_OUT(1 << 12, "Spun Out", "SO"),
  AUTOPILOT(1 << 13, "Autopilot", "AP"),
  PERFECT(1 << 14, "Perfect", "PF"),
  KEY4(1 << 15, "4K", "4K"),
  KEY5(1 << 16, "5K", "5K"),
  KEY6(1 << 17, "6K", "6K"),
  KEY7(1 << 18, "7K", "7K"),
  KEY8(1 << 19, "8K", "8K"),
  FADE_IN(1 << 20, "Fade-In", "FI"),
  RANDOM(1 << 21, "Random", "RD"),
  CINEMA(1 << 22, "Cinema", "CN"),
  TARGET(1 << 23, "Target Practice", "TC"),
  KEY9(1 << 24, "9K", "9K"),
  KEY_COOP(1 << 25, "Co-Op", "CO"),
  KEY1(1 << 26, "1K", "1K"),
  KEY3(1 << 27, "3K", "3K"),
  KEY2(1 << 28, "2K", "2K"),
  SCORE_V2(1 << 29, "Score V2", "V2"),
  MIRROR(1 << 30, "Mirror", "MR"),
  ;

  private final int value;
  private final String displayName;
  private final String initial;

  public static List<Mods> fromBitmask(int bit) {
    List<Mods> values =
        Arrays.stream(values())
            .sorted(Comparator.comparingLong(Mods::getValue))
            .sorted(Comparator.reverseOrder())
            .toList();
    List<Mods> mods = new ArrayList<>();

    while (bit > 0)
      for (Mods mod : values)
        if (mod.getValue() <= bit) {
          mods.add(mod);
          bit -= mod.getValue();
        }

    return mods;
  }

  public static List<Mods> fromInitials(String[] initials) {
    return Arrays.stream(values())
        .filter(
            mod -> {
              for (String initial : initials) {
                if (mod.getInitial().equalsIgnoreCase(initial)) {
                  return true;
                }
              }
              return false;
            })
        .collect(Collectors.toList());
  }

  public static int toBitmask(List<Mods> mods) {
    int bitmask = 0;
    for (Mods mod : mods) {
      bitmask |= mod.getValue();
    }
    return bitmask;
  }
}
