package pe.nanamochi.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import pe.nanamochi.BanchoPacket;

public interface IBanchoIO extends IBanchoWriters {

  void writePacket(OutputStream stream, int packetId, byte[] data) throws IOException;

  void writePacket(OutputStream stream, int packetId, byte[] data, boolean includeMTime)
      throws IOException;

  BanchoPacket readPacket(InputStream stream) throws IOException;

  boolean supportsPacket(int packetId);
}
