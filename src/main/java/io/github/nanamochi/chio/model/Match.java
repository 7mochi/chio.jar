package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.Mode;
import io.github.nanamochi.chio.model.enums.ScoringType;
import io.github.nanamochi.chio.model.enums.TeamType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
  private int id;
  private boolean inProgress;
  private MatchType type;
  private int mods;
  private String name;
  private String password;
  private String beatmapText;
  private int beatmapId;
  private String beatmapChecksum;
  private List<MatchSlot> slots;
  private int hostId;
  private Mode mode;
  private ScoringType scoringType;
  private TeamType teamType;
  private boolean freemod;
  private int seed;
}
