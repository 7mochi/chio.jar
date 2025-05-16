package pe.nanamochi.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import pe.nanamochi.objects.enums.QuitState;

@Data
@AllArgsConstructor
public class UserQuit {

  private UserInfo info;
  private QuitState state;
}
