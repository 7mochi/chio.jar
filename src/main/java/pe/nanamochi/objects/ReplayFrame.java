package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.nanamochi.objects.enums.ButtonState;

@Data
@AllArgsConstructor
public class ReplayFrame {

  private ButtonState buttonState;
  private double mouseX;
  private double mouseY;
  private int time;
}
