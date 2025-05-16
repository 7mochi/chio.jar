package pe.nanamochi.objects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import pe.nanamochi.objects.enums.ReplayAction;

@Data
@AllArgsConstructor
public class ReplayFrameBundle {

  private ReplayAction action;
  private List<ReplayFrame> frames;
  private ScoreFrame frame;
  private int extra;
}
