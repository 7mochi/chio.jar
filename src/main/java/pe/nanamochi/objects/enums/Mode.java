package pe.nanamochi.objects.enums;

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

  private static final Map<String, Mode> ALIAS_MAP = new HashMap<>();

  static {
    ALIAS_MAP.put("std", OSU);
    ALIAS_MAP.put("osu", OSU);
    ALIAS_MAP.put("taiko", TAIKO);
    ALIAS_MAP.put("fruits", CATCH);
    ALIAS_MAP.put("ctb", CATCH);
    ALIAS_MAP.put("catch", CATCH);
    ALIAS_MAP.put("mania", MANIA);
  }

  public static Mode fromAlias(String input) {
    return ALIAS_MAP.get(input.toLowerCase());
  }
}
