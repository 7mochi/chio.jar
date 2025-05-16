package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStats {

  private int rank;
  private int rankedScore;
  private int totalScore;
  private double accuracy;
  private int playcount;
  private int pp;
}
