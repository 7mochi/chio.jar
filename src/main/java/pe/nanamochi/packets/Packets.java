package pe.nanamochi.packets;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Packets {

  // Client
  OSU_USER_STATUs(0),
  OSU_MESSAGE(1),
  OSU_EXIT(2),
  OSU_STATUS_UPDATE_REQUEST(3),
  OSU_PONG(4),
  OSU_START_SPECTATING(16),
  OSU_STOP_SPECTATING(17),
  OSU_SPECTATE_FRAMES(18),
  OSU_ERROR_REPORT(20),
  OSU_CANT_SPECTATE(21),
  OSU_PRIVATE_MESSAGE(25),

  // Server
  BANCHO_LOGIN_REPLY(5),
  BANCHO_COMMAND_ERROR(6),
  BANCHO_MESSAGE(7),
  BANCHO_PING(8),
  BANCHO_IRC_CHANGE_USERNAME(9),
  BANCHO_IRC_QUIT(10),
  BANCHO_USER_STATS(11),
  BANCHO_USER_QUIT(12),
  BANCHO_SPECTATOR_JOINED(13),
  BANCHO_SPECTATOR_LEFT(14),
  BANCHO_SPECTATE_FRAMES(15),
  BANCHO_VERSION_UPDATE(19),
  BANCHO_SPECTATOR_CANT_SPECTATE(22),
  BANCHO_GET_ATTENTION(23),
  BANCHO_ANNOUNCE(24),

  // Legacy packets
  BANCHO_HANDLE_IRC_JOIN(65535);

  private final int id;

  public static Packets fromId(int id) {
    for (Packets type : values()) {
      if (type.id == id) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown packet id: " + id);
  }
}
