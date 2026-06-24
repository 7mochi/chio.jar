package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.model.enums.SlotStatus;
import io.github.nanamochi.chio.model.enums.SlotTeam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatchSlot {
  private int userId;
  private SlotStatus status;
  private SlotTeam team;
  private int mods;

  public boolean hasPlayer() {
    return (status.getValue() & SlotStatus.HAS_PLAYER.getValue()) != 0;
  }
}
