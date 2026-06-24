package io.github.nanamochi.chio.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserStats {
  private int rank;
  private int rankedScore;
  private int totalScore;
  private float accuracy;
  private int playcount;
  private int performancePoints;
}
