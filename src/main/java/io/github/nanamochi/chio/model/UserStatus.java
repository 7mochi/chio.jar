package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.model.enums.Mode;
import io.github.nanamochi.chio.model.enums.Mods;
import io.github.nanamochi.chio.model.enums.Status;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatus {
  private Status action;
  private String text;
  private List<Mods> mods;
  private Mode mode;
  private String beatmapChecksum;
  private int beatmapId;
  private boolean updateStats;
}
