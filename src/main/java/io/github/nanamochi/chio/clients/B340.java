package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.IOException;

// b340 removes the "MatchChangeBeatmap" packet, and introduces the "MatchHasBeatmap" packet.
public class B340 extends B338 {

  // region Packet Reading

  @Override
  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    if (type == PacketType.OSU_MATCH_HAS_BEATMAP) {
      return null;
    }
    return super.readPacketType(type, data);
  }

  // endregion
  // region Packet Conversion

  @Override
  protected PacketType convertInputPacket(int packet) {
    if (packet == 11) return PacketType.BANCHO_IRC_JOIN;
    if (packet > 11) packet--;

    return PacketType.fromValue(packet);
  }

  @Override
  protected int convertOutputPacket(PacketType packet) {
    if (packet == PacketType.BANCHO_IRC_JOIN) return 11;

    int value = packet.getValue();

    if (value >= 11) return value + 1;

    return value;
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 340;
  }

  // endregion
}
