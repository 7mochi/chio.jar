package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Message {

  private String sender;
  private String content;
  private String target;
  private int senderId;
}
