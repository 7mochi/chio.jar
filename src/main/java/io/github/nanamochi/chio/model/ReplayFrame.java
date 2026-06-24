package io.github.nanamochi.chio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplayFrame {
  private int buttonState;
  private float mouseX;
  private float mouseY;
  private int time;
}
