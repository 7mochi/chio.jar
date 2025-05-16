package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchSlot {

  private int userId;
  private int status;
  private int team;
  private int mods;
}
