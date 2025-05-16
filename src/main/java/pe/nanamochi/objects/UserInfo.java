package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {

  private int id;
  private String name;
  private UserPresence presence;
  private UserStatus status;
  private UserStats stats;

  public String getAvatarFilename() {
    return this.getId() + "_000.png";
  }
}
