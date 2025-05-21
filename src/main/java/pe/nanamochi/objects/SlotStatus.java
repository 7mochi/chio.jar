package pe.nanamochi.objects;

import java.util.EnumSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SlotStatus {
  OPEN(1),
  LOCKED(1 << 1),
  NOT_READY(1 << 2),
  READY(1 << 3),
  NO_MAP(1 << 4),
  PLAYING(1 << 5),
  COMPLETE(1 << 6),
  QUIT(1 << 7),

  HAS_PLAYER(NOT_READY.value | READY.value | NO_MAP.value | PLAYING.value | COMPLETE.value);

  private final int value;

  public static Set<SlotStatus> fromBits(int bits) {
    Set<SlotStatus> result = EnumSet.noneOf(SlotStatus.class);
    for (SlotStatus status : values()) {
      if ((status.value & bits) == status.value) {
        result.add(status);
      }
    }
    return result;
  }

  public static int toBits(Set<SlotStatus> statuses) {
    int bits = 0;
    for (SlotStatus status : statuses) {
      bits |= status.value;
    }
    return bits;
  }

  public boolean isSet(int bits) {
    return (bits & this.value) == this.value;
  }
}
