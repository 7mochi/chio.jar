package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// b291 implements the GetAttention & Announce packets.
public class B291 extends B282 {

  // region Packet Writing

  @Override
  public byte[] writePacket(PacketType type, Object... args) throws IOException {
    return switch (type) {
      case BANCHO_GET_ATTENTION -> writeGetAttention();
      case BANCHO_ANNOUNCE -> writeAnnounce((String) args[0]);
      case BANCHO_RESTART -> writeRestart(args.length > 0 ? (int) args[0] : 5000);
      default -> super.writePacket(type, args);
    };
  }

  protected byte[] writeGetAttention() throws IOException {
    return writePacket(PacketType.BANCHO_GET_ATTENTION, new byte[0]);
  }

  protected byte[] writeAnnounce(String message) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeString(buf, message);
    return writePacket(PacketType.BANCHO_ANNOUNCE, buf.toByteArray());
  }

  // NOTE: This is a backport of the actual restart packet, that
  // simply announces the server restart to the user.
  protected byte[] writeRestart(int retryAfterMs) throws IOException {
    return writeAnnounce("Bancho is restarting, please wait...");
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 291;
  }

  // endregion
}
