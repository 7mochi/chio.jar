package io.github.nanamochi.chio.model;

import io.github.nanamochi.chio.model.enums.QuitState;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserQuit {
  private UserInfo info;
  private QuitState state;
}
