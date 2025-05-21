package pe.nanamochi.objects;

public enum SlotTeam {
  NEUTRAL(0),
  BLUE(1),
  RED(2);

  private final int value;

  SlotTeam(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

  public SlotTeam getOpposite() {
    if (this == RED) {
      return BLUE;
    } else if (this == BLUE) {
      return RED;
    } else {
      return NEUTRAL;
    }
  }

  public static SlotTeam fromValue(int value) {
    for (SlotTeam team : values()) {
      if (team.value == value) {
        return team;
      }
    }
    throw new IllegalArgumentException("Unknown SlotTeam value: " + value);
  }
}
