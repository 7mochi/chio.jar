package pe.nanamochi.clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import pe.nanamochi.packets.Packets;

/***
 * B291 implements the GetAttention & Announce packets.
 */
public class B291 extends B282 {

  public B291(int slotSize, int protocolVersion) {
    super(slotSize, protocolVersion);
  }

  @Override
  public void writeGetAttention(OutputStream stream) throws IOException {
    writePacket(
        stream, Packets.BANCHO_GET_ATTENTION.getId(), new ByteArrayOutputStream().toByteArray());
  }

  @Override
  public void writeAnnouncement(OutputStream stream, String message) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeString(buffer, message);
    writePacket(stream, Packets.BANCHO_ANNOUNCE.getId(), buffer.toByteArray());
  }

  @Override
  public void writeRestart(OutputStream stream, int retryMs) throws IOException {
    // NOTE: This is a backport of the actual restart packet, that simply announces
    // the server restart to the user.
    writeAnnouncement(stream, "Bancho is restarting, please wait...");
  }
}
