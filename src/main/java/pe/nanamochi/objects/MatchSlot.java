package pe.nanamochi.objects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import pe.nanamochi.objects.enums.Mods;

@Data
@AllArgsConstructor
public class MatchSlot {

  private int userId;
  private SlotStatus status;
  private SlotTeam team;
  private List<Mods> mods;

  public boolean hasPlayer() {
    return (status.getValue() & SlotStatus.HAS_PLAYER.getValue()) != 0;
  }
}
