package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BeatmapInfo {

  private int index;
  private int beatmapId;
  private int beatmapSetId;
  private int threadId;
  private int rankedStatus;
  private int osuRank;
  private int taikoRank;
  private int fruitsRank;
  private int maniaRank;
  private String checksum;
}
