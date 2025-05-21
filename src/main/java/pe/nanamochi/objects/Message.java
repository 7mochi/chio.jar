package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

  private String sender;
  private String content;
  private String target;
  private int senderId;

  public boolean isDirectMessage() {
    return !this.target.startsWith("#");
  }
}
