package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.nanamochi.utils.Hash;

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
    return this.getHp() != 254;
  }

  public String getChecksum() {
    String data =
        String.format(
            "%d%s%d%d%d%d%d%d%d%d",
            this.time,
            this.isPassed() ? "True" : "False",
            this.total300,
            this.total50,
            this.totalGeki,
            this.totalKatu,
            this.totalMiss,
            this.currentCombo,
            this.maxCombo,
            this.hp);
    return Hash.md5Hex(data);
  }
}
