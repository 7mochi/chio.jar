package pe.nanamochi.objects.enums;

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

  private final int id;

  public static Permissions fromId(int id) {
    for (Permissions type : values()) {
      if (type.id == id) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown permissions id: " + id);
  }
}
