package pe.nanamochi.objects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
  IDLE(0),
  AFK(1),
  PLAYING(2),
  EDITING(3),
  MODDING(4),
  MULTIPLAYER(5),
  WATCHING(6),
  UNKNOWN(7),
  TESTING(8),
  SUBMITTING(9),
  PAUSED(10),
  LOBBY(11),
  MULTIPLAYING(12),
  OSU_DIRECT(13),

  // Unused in later versions, but required for compatibility
  STATS_UPDATE(10);

  private final int value;

  public static Status fromValue(int value) {
    for (Status status : values()) {
      if (status.value == value) {
        return status;
      }
    }
    throw new IllegalArgumentException("Unknown status value: " + value);
  }
}
