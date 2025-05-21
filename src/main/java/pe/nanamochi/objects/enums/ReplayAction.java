package pe.nanamochi.objects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReplayAction {
  STANDARD(0),
  NEW_SONG(1),
  SKIP(2),
  COMPLETION(3),
  FAIL(4),
  PAUSE(5),
  UNPAUSE(6),
  SONG_SELECT(7),
  WATCHING_OTHER(8);

  private final int value;

  public static ReplayAction fromValue(int value) {
    for (ReplayAction action : ReplayAction.values()) {
      if (action.getValue() == value) {
        return action;
      }
    }
    return null;
  }
}
