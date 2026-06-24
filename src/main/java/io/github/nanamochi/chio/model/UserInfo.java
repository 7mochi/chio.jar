package io.github.nanamochi.chio.model;

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
    return getId() + "_000.png";
  }

  public String getAvatarFilename(String extension) {
    return getId() + "_000." + extension.trim();
  }
}
