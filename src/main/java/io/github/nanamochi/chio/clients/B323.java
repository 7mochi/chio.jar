package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.UserInfo;
import io.github.nanamochi.chio.model.enums.PacketType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// b323 changes the structure of user stats and adds the "MatchChangeBeatmap" packet
public class B323 extends B320 {

  // region Packet Reading

  @Override
  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    if (type == PacketType.OSU_MATCH_CHANGE_BEATMAP) {
      return readMatchChangeBeatmap(data);
    }
    return super.readPacketType(type, data);
  }

  // endregion
  // region Reader Methods

  protected Match readMatchChangeBeatmap(byte[] data) throws IOException {
    return readMatch(data);
  }

  // endregion
  // region Packet Conversion

  @Override
  protected PacketType convertInputPacket(int packet) {
    if (packet == 11) return PacketType.BANCHO_IRC_JOIN;
    if (packet == 50) return PacketType.OSU_MATCH_CHANGE_BEATMAP;
    if (packet > 11 && packet <= 45) packet--;

    return PacketType.fromValue(packet);
  }

  @Override
  protected int convertOutputPacket(PacketType packet) {
    if (packet == PacketType.BANCHO_IRC_JOIN) return 11;
    if (packet == PacketType.OSU_MATCH_CHANGE_BEATMAP) return 50;

    int value = packet.getValue();

    if (value >= 11 && value < 45) return value + 1;

    return value;
  }

  // endregion
  // region Writer Methods

  @Override
  protected byte[] writeUserStats(UserInfo info) throws IOException {
    if (info.getPresence().isIrc()) {
      return writeIrcJoin(info.getName());
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    boolean writeStats = info.getStatus().isUpdateStats();

    writer.writeUInt32(buf, info.getId());
    writer.writeBoolean(buf, writeStats);

    if (writeStats) {
      writer.writeString(buf, info.getName());
      writer.writeUInt64(buf, info.getStats().getRankedScore());
      writer.writeFloat32(buf, info.getStats().getAccuracy());
      writer.writeUInt32(buf, info.getStats().getPlaycount());
      writer.writeUInt64(buf, info.getStats().getTotalScore());
      writer.writeUInt32(buf, info.getStats().getRank());
      writer.writeString(buf, info.getAvatarFilename());
      writer.writeUInt8(buf, info.getPresence().getTimezone() + 24);
      writer.writeString(buf, info.getPresence().getLocation());
    }

    buf.write(writeStatusUpdate(info.getStatus()));
    return writePacket(PacketType.BANCHO_USER_STATS, buf.toByteArray());
  }

  @Override
  protected byte[] writeUserPresence(UserInfo info) throws IOException {
    if (info.getPresence().isIrc()) {
      return writeIrcJoin(info.getName());
    }

    // We assume that the client has not seen this user before, so
    // we send two packets: one for the user stats, and one for the "presence".
    ByteArrayOutputStream buf = new ByteArrayOutputStream();

    info.getStatus().setUpdateStats(true);
    buf.write(writeUserStats(info));

    info.getStatus().setUpdateStats(false);
    buf.write(writeUserStats(info));

    return buf.toByteArray();
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 323;
  }

  // endregion
}
