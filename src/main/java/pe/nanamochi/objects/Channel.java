package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Channel {

  private String name;
  private String topic;
  private String owner;
  private int userCount;
}
