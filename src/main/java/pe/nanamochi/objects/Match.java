package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Match {

  private int id;
  private boolean inProgress;
  private int type;
  private int mods;
  private String name;
  private String password;
  private String beatmapText;
  private int beatmapId;
  private String beatmapChecksum;
  private MatchSlot slots;
  private int hostId;
  private int mode;
  private int scoringType;
  private int teamType;
  private boolean freemod;
  private int seed;
}
