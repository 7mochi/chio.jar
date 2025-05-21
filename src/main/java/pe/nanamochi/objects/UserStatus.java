package pe.nanamochi.objects;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pe.nanamochi.objects.enums.Mode;
import pe.nanamochi.objects.enums.Mods;
import pe.nanamochi.objects.enums.Status;

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
