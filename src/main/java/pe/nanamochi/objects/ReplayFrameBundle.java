package pe.nanamochi.objects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.nanamochi.objects.enums.ReplayAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplayFrameBundle {

  private ReplayAction action;
  private List<ReplayFrame> frames;
  private ScoreFrame frame;
  private int extra;
}
