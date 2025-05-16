package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScoreFrame {

  private int Time;
  private int id;
  private int total300;
  private int total100;
  private int total50;
  private int totalGeki;
  private int totalKatu;
  private int totalMiss;
  private int totalScore;
  private int maxCombo;
  private int currentCombo;
  private boolean perfect;
  private int hp;
  private int tagByte;
}
