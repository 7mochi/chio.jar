package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.model.enums.ReplayAction;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplayFrameBundle {
  private ReplayAction action;
  private List<ReplayFrame> frames;
  private ScoreFrame frame;
  private int extra;
}
