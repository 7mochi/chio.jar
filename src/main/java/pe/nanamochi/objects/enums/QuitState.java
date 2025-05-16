package pe.nanamochi.objects.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuitState {
  GONE(0),
  OSU_REMAINING(1),
  IRC_REMAINING(2);

  private final int value;
}
