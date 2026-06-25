package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.UserInfo;
import io.github.nanamochi.chio.model.UserStatus;
import io.github.nanamochi.chio.model.enums.Completeness;
import io.github.nanamochi.chio.model.enums.Mods;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.Status;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// b338 changes the structure of statuses & stats.
public class B338 extends B334 {

  // region Reader Methods

  @Override
  protected UserStatus readUserStatus(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);

    UserStatus status = new UserStatus();
    status.setAction(Status.fromValue(reader.readUInt8(input)));
    boolean beatmapUpdate = reader.readBoolean(input);

    if (beatmapUpdate) {
      status.setText(reader.readString(input));
      status.setBeatmapChecksum(reader.readString(input));
      status.setMods(Mods.fromBitmask(reader.readUInt16(input)));
    }

    // There is a bug where the client starts playing but doesn't set the status to "Playing".
    if (status.getAction() == Status.IDLE && !status.getBeatmapChecksum().isEmpty()) {
      status.setAction(Status.PLAYING);
    }

    return status;
  }

  // endregion
  // region Writer Methods

  @Override
  protected byte[] writeUserStats(UserInfo info) throws IOException {
    if (info.getPresence().isIrc()) {
      return writeIrcJoin(info.getName());
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt32(buf, info.getId());
    writer.writeUInt8(buf, Completeness.STATISTICS.getValue());
    buf.write(writeStatusUpdate(info.getStatus()));
    writer.writeUInt64(buf, info.getStats().getRankedScore());
    writer.writeFloat32(buf, info.getStats().getAccuracy());
    writer.writeUInt32(buf, info.getStats().getPlaycount());
    writer.writeUInt64(buf, info.getStats().getTotalScore());
    writer.writeUInt16(buf, info.getStats().getRank());
    return writePacket(PacketType.BANCHO_USER_STATS, buf.toByteArray());
  }

  @Override
  protected byte[] writeUserPresence(UserInfo info) throws IOException {
    if (info.getPresence().isIrc()) {
      return writeIrcJoin(info.getName());
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt32(buf, info.getId());
    writer.writeUInt8(buf, Completeness.FULL.getValue());
    buf.write(writeStatusUpdate(info.getStatus()));
    writer.writeUInt64(buf, info.getStats().getRankedScore());
    writer.writeFloat32(buf, info.getStats().getAccuracy());
    writer.writeUInt32(buf, info.getStats().getPlaycount());
    writer.writeUInt64(buf, info.getStats().getTotalScore());
    writer.writeUInt16(buf, info.getStats().getRank());
    writer.writeString(buf, info.getName());
    writer.writeString(buf, info.getAvatarFilename());
    writer.writeUInt8(buf, info.getPresence().getTimezone() + 24);
    writer.writeString(buf, info.getPresence().getLocation());
    return writePacket(PacketType.BANCHO_USER_STATS, buf.toByteArray());
  }

  @Override
  protected byte[] writeStatusUpdate(UserStatus status) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt8(buf, status.getAction().getValue());

    boolean beatmapUpdate = true;
    writer.writeBoolean(buf, beatmapUpdate);

    if (beatmapUpdate) {
      writer.writeString(buf, status.getText());
      writer.writeString(buf, status.getBeatmapChecksum());
      writer.writeUInt16(buf, Mods.toBitmask(status.getMods()));
    }

    return buf.toByteArray();
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 338;
  }

  // endregion
}
