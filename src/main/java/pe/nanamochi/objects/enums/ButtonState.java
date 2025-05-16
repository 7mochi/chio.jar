package pe.nanamochi.objects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ButtonState {
  NO_BUTTON(0),
  LEFT_1(1),
  RIGHT_1(1 << 1),
  LEFT_2(1 << 2),
  RIGHT_2(1 << 3),
  SMOKE(1 << 4);

  private final int value;

  public boolean isSet(int state) {
    return (state & value) != 0;
  }
}
