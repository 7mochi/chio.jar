package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.util.Hash;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreFrame {
  private int time;
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

  public boolean isPassed() {
    return hp != 254;
  }

  public String getChecksum() {
    String data =
        String.format(
            "%d%s%d%d%d%d%d%d%d%d",
            time,
            isPassed() ? "True" : "False",
            total300,
            total50,
            totalGeki,
            totalKatu,
            totalMiss,
            currentCombo,
            maxCombo,
            hp);
    return Hash.md5Hex(data);
  }
}
