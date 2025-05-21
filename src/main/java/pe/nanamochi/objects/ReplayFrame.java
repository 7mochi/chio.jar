package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplayFrame {

  private int buttonState;
  private double mouseX;
  private double mouseY;
  private int time;
}
