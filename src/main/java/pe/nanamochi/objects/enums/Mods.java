package pe.nanamochi.objects.enums;

import java.util.EnumSet;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Mods {
  NoMod(0),
  NoFail(1),
  Easy(1 << 1),
  NoVideo(1 << 2),
  Hidden(1 << 3),
  HardRock(1 << 4),
  SuddenDeath(1 << 5),
  DoubleTime(1 << 6),
  Relax(1 << 7),
  HalfTime(1 << 8),
  Nightcore(1 << 9),
  Flashlight(1 << 10),
  Autoplay(1 << 11),
  SpunOut(1 << 12),
  Autopilot(1 << 13),
  Perfect(1 << 14),
  Key4(1 << 15),
  Key5(1 << 16),
  Key6(1 << 17),
  Key7(1 << 18),
  Key8(1 << 19),
  FadeIn(1 << 20),
  Random(1 << 21),
  Cinema(1 << 22),
  Target(1 << 23),
  Key9(1 << 24),
  KeyCoop(1 << 25),
  Key1(1 << 26),
  Key3(1 << 27),
  Key2(1 << 28),
  ScoreV2(1 << 29),
  Mirror(1 << 30);

  private final int value;

  public static EnumSet<Mods> fromBitmask(int bitmask) {
    EnumSet<Mods> set = EnumSet.noneOf(Mods.class);
    for (Mods mod : values()) {
      if ((bitmask & mod.value) != 0) {
        set.add(mod);
      }
    }
    return set;
  }

  public static int toBitmask(EnumSet<Mods> mods) {
    int result = 0;
    for (Mods mod : mods) {
      result |= mod.value;
    }
    return result;
  }

  public static EnumSet<Mods> keyMods() {
    return EnumSet.of(Key1, Key2, Key3, Key4, Key5, Key6, Key7, Key8, Key9, KeyCoop);
  }

  public static EnumSet<Mods> freeModAllowed() {
    EnumSet<Mods> set =
        EnumSet.of(
            NoFail,
            Easy,
            Hidden,
            HardRock,
            SuddenDeath,
            Flashlight,
            FadeIn,
            Relax,
            Autopilot,
            SpunOut);
    set.addAll(keyMods());
    return set;
  }

  public static EnumSet<Mods> speedMods() {
    return EnumSet.of(DoubleTime, HalfTime, Nightcore);
  }
}
