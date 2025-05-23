package pe.nanamochi.clients;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.nanamochi.io.IBanchoIO;
import pe.nanamochi.io.data.BanchoDataReader;
import pe.nanamochi.io.data.BanchoDataWriter;
import pe.nanamochi.io.data.IDataReader;
import pe.nanamochi.io.data.IDataWriter;
import pe.nanamochi.objects.*;
import pe.nanamochi.objects.enums.*;
import pe.nanamochi.packets.Packets;
import pe.nanamochi.utils.GZip;

/***
 * B282 is the initial implementation of the bancho protocol.
 * Every following version will be based on it.
 */
@Getter
@Setter
public class B282 implements IBanchoIO {

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected final IDataReader reader;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  protected final IDataWriter writer;

  protected int protocolVersion;
  protected int slotSize;

  public B282(int slotSize, int protocolVersion) {
    this.reader = new BanchoDataReader();
    this.writer = new BanchoDataWriter();

    this.slotSize = slotSize;
    this.protocolVersion = protocolVersion;
  }

  @Override
  public void writePacket(OutputStream stream, int packetId, byte[] data) throws IOException {
    writePacket(stream, packetId, data, false);
  }

  @Override
  public void writePacket(OutputStream stream, int packetId, byte[] data, boolean includeMTime)
      throws IOException {
    logger.info("Wrote packet: " + Packets.fromId(packetId).name() + " (" + packetId + ")");

    packetId = convertOutputPacketId(packetId);

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    writer.writeUint16(buffer, packetId);

    byte[] compressed = GZip.compress(data, includeMTime);

    writer.writeUint32(buffer, compressed.length);

    buffer.write(compressed);
    stream.write(buffer.toByteArray());
  }

  @Override
  public Object readPacket(InputStream stream) throws IOException {
    int packetId = reader.readUint16(stream);
    packetId = convertInputPacketId(packetId);

    logger.info("Read packet: " + Packets.fromId(packetId).name() + " (" + packetId + ")");

    int length = reader.readInt32(stream);

    byte[] compressedData = stream.readNBytes(length);
    byte[] decompressedData = GZip.decompress(compressedData);

    return readPacketType(packetId, new ByteArrayInputStream(decompressedData));
  }

  /**
   * Convert a modern packet type from the server that the client can understand.
   *
   * @param packetId Modern packet ID from the server.
   * @return Packet ID in the B282 format.
   */
  public int convertOutputPacketId(int packetId) {
    if (packetId == Packets.BANCHO_HANDLE_IRC_JOIN.getId()) {
      return 11;
    }
    if (packetId >= 11 && packetId < 45) {
      return (short) (packetId + 1);
    }
    if (packetId > 50) {
      packetId += 1;
    }
    return packetId;
  }

  public int convertInputPacketId(int packetId) {
    if (packetId == 11) {
      return Packets.BANCHO_HANDLE_IRC_JOIN.getId();
    }
    if (packetId > 11 && packetId <= 45) {
      packetId -= 1;
    }
    if (packetId > 50) {
      packetId -= 1;
    }
    return packetId;
  }

  public Object readPacketType(int packetId, InputStream stream) throws IOException {
    if (packetId == Packets.OSU_USER_STATUS.getId()) {
      return readUserStatus(stream);
    } else if (packetId == Packets.OSU_MESSAGE.getId()) {
      return readMessage(stream);
    } else if (packetId == Packets.OSU_START_SPECTATING.getId()) {
      return reader.readUint32(stream);
    } else if (packetId == Packets.OSU_SPECTATE_FRAMES.getId()) {
      return readSpectateFrames(stream);
    } else if (packetId == Packets.OSU_ERROR_REPORT.getId()) {
      return reader.readString(stream);
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public void writeLoginReply(OutputStream stream, int reply) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, reply);
    writePacket(stream, Packets.BANCHO_LOGIN_REPLY.getId(), buffer.toByteArray());
  }

  @Override
  public void writeMessage(OutputStream stream, Message message) throws IOException {
    if ("#osu".equals(message.getTarget())) {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      writer.writeString(buffer, message.getSender());
      writer.writeString(buffer, message.getContent());
      writePacket(stream, Packets.BANCHO_MESSAGE.getId(), buffer.toByteArray());
    }
  }

  @Override
  public void writePing(OutputStream stream) throws IOException {
    writePacket(stream, Packets.BANCHO_PING.getId(), new ByteArrayOutputStream().toByteArray());
  }

  @Override
  public void writeIrcChangeUsername(OutputStream stream, String oldName, String newName)
      throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeString(buffer, oldName + ">>>>" + newName);
    writePacket(stream, Packets.BANCHO_IRC_CHANGE_USERNAME.getId(), buffer.toByteArray());
  }

  @Override
  public void writeUserStats(OutputStream stream, UserInfo info) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    if (info.getPresence().isIrc()) {
      writer.writeString(buffer, info.getName());
      writePacket(stream, Packets.BANCHO_HANDLE_IRC_JOIN.getId(), buffer.toByteArray());
      return;
    }

    byte[] rawStats = encodeUserStats(info);
    writePacket(stream, Packets.BANCHO_USER_STATS.getId(), rawStats);
  }

  private byte[] encodeUserStats(UserInfo info) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    writer.writeUint32(buffer, info.getId());
    writer.writeString(buffer, info.getName());
    writer.writeUint64(buffer, info.getStats().getRankedScore());
    writer.writeFloat64(buffer, info.getStats().getAccuracy());
    writer.writeUint32(buffer, info.getStats().getPlaycount());
    writer.writeUint64(buffer, info.getStats().getTotalScore());
    writer.writeUint32(buffer, info.getStats().getRank());
    writer.writeString(buffer, info.getAvatarFilename());

    writeStatus(buffer, info.getStatus());

    writer.writeUint8(buffer, info.getPresence().getTimezone() + 24);
    writer.writeString(buffer, info.getPresence().getLocation());

    return buffer.toByteArray();
  }

  public void writeStatus(OutputStream stream, UserStatus status) throws IOException {
    int action = status.getAction().getValue();

    if (status.isUpdateStats()) {
      // This will make the client update the user's stats
      // It will not be present in later versions
      action = Status.STATS_UPDATE.getValue();
    } else if (action > Status.SUBMITTING.getValue()) {
      action = action - 1;
    }

    writer.writeUint8(stream, action);

    if (action != Status.UNKNOWN.getValue()) {
      writer.writeString(stream, status.getText());
      writer.writeString(stream, status.getBeatmapChecksum());
      writer.writeUint16(stream, Mods.toBitmask(status.getMods()));
    }
  }

  @Override
  public void writeUserQuit(OutputStream stream, UserQuit quit) throws IOException {
    if (quit.getState() == QuitState.OSU_REMAINING) {
      return;
    }

    if (quit.getInfo().getPresence().isIrc()) {
      if (quit.getState() != QuitState.IRC_REMAINING) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writer.writeString(buffer, quit.getInfo().getName());
        writePacket(stream, Packets.BANCHO_IRC_QUIT.getId(), buffer.toByteArray());
        return;
      } else {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        writer.writeString(buffer, quit.getInfo().getName());
        writePacket(stream, Packets.BANCHO_USER_QUIT.getId(), buffer.toByteArray());
        return;
      }
    }

    byte[] rawStats = encodeUserStats(quit.getInfo());
    writePacket(stream, Packets.BANCHO_USER_QUIT.getId(), rawStats);
  }

  @Override
  public void writeSpectatorJoined(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeUint32(buffer, userId);
    writePacket(stream, Packets.BANCHO_SPECTATOR_JOINED.getId(), buffer.toByteArray());
  }

  @Override
  public void writeSpectatorLeft(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeUint32(buffer, userId);
    writePacket(stream, Packets.BANCHO_SPECTATOR_LEFT.getId(), buffer.toByteArray());
  }

  @Override
  public void writeSpectateFrames(OutputStream stream, ReplayFrameBundle bundle)
      throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeUint16(buffer, bundle.getFrames().size());

    for (ReplayFrame frame : bundle.getFrames()) {
      int buttonState = frame.getButtonState();
      boolean leftMouse =
          ButtonState.LEFT_1.isSet(buttonState) || ButtonState.LEFT_2.isSet(buttonState);
      boolean rightMouse =
          ButtonState.RIGHT_1.isSet(buttonState) || ButtonState.RIGHT_2.isSet(buttonState);

      writer.writeBoolean(buffer, leftMouse);
      writer.writeBoolean(buffer, rightMouse);
      writer.writeFloat32(buffer, (float) frame.getMouseX());
      writer.writeFloat32(buffer, (float) frame.getMouseY());
      writer.writeInt32(buffer, frame.getTime());
    }

    writer.writeUint8(buffer, bundle.getAction().getValue());
    writePacket(stream, Packets.BANCHO_SPECTATE_FRAMES.getId(), buffer.toByteArray());
  }

  @Override
  public void writeVersionUpdate(OutputStream stream) throws IOException {
    writePacket(
        stream, Packets.BANCHO_VERSION_UPDATE.getId(), new ByteArrayOutputStream().toByteArray());
  }

  @Override
  public void writeSpectatorCantSpectate(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeUint32(buffer, userId);
    writePacket(stream, Packets.BANCHO_SPECTATOR_CANT_SPECTATE.getId(), buffer.toByteArray());
  }

  @Override
  public void writeUserPresence(OutputStream stream, UserInfo info) throws IOException {
    // B282 does not support user presences,
    // instead we will send a stats update
    writeUserStats(stream, info);
  }

  @Override
  public void writeUserPresenceSingle(OutputStream stream, UserInfo info) throws IOException {
    writeUserPresence(stream, info);
  }

  @Override
  public void writeUserPresenceBundle(OutputStream stream, List<UserInfo> infos)
      throws IOException {
    for (UserInfo info : infos) {
      writeUserPresenceSingle(stream, info);
    }
  }

  @Override
  public void writeGetAttention(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeAnnouncement(OutputStream stream, String message) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchUpdate(OutputStream stream, Match match) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchNew(OutputStream stream, Match match) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchDisband(OutputStream stream, int matchId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeLobbyJoin(OutputStream stream, int userId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeLobbyPart(OutputStream stream, int userId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchJoinSuccess(OutputStream stream, Match match) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchJoinFail(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeFellowSpectatorJoined(OutputStream stream, int userId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeFellowSpectatorLeft(OutputStream stream, int userId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchStart(OutputStream stream, Match match) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchScoreUpdate(OutputStream stream, ScoreFrame frame) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchTransferHost(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchAllPlayersLoaded(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchPlayerFailed(OutputStream stream, long slotId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchComplete(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchSkip(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeUnauthorized(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeChannelJoinSuccess(OutputStream stream, String channel) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeChannelRevoked(OutputStream stream, String channel) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeChannelAvailable(OutputStream stream, Channel channel) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeChannelAvailableAutojoin(OutputStream stream, Channel channel)
      throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeBeatmapInfoReply(OutputStream stream, BeatmapInfoReply reply)
      throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeLoginPermissions(OutputStream stream, long permissions) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeFriendsList(OutputStream stream, List<Integer> userIds) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeProtocolNegotiation(OutputStream stream, int version) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeTitleUpdate(OutputStream stream, TitleUpdate update) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMonitor(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchPlayerSkipped(OutputStream stream, int slotId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeRestart(OutputStream stream, int retryMs) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeInvite(OutputStream stream, Message message) throws IOException {
    // B282 does not support invites, so instead
    // we will send the message directly
    writeMessage(stream, message);
  }

  @Override
  public void writeChannelInfoComplete(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchChangePassword(OutputStream stream, String password) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeSilenceInfo(OutputStream stream, int timeRemaining) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeUserSilenced(OutputStream stream, long userId) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeUserDMsBlocked(OutputStream stream, String targetName) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeTargetIsSilenced(OutputStream stream, String targetName) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeVersionUpdateForced(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeSwitchServer(OutputStream stream, int target) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeAccountRestricted(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeRTX(OutputStream stream, String message) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeMatchAbort(OutputStream stream) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  @Override
  public void writeSwitchTournamentServer(OutputStream stream, String ip) throws IOException {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  public UserStatus readUserStatus(InputStream stream) throws IOException {
    UserStatus userStatus = new UserStatus();
    Status status = Status.fromValue(reader.readUint8(stream));

    if (status.getValue() == 10) {
      status = Status.UNKNOWN;
    } else if (status.getValue() > 9) {
      status = Status.fromValue(status.getValue() - 1);
    }

    userStatus.setAction(status);

    if (userStatus.getAction() != Status.UNKNOWN) {
      userStatus.setText(reader.readString(stream));
      userStatus.setBeatmapChecksum(reader.readString(stream));
      userStatus.setMods(Mods.fromBitmask(reader.readUint16(stream)));
    }

    if (userStatus.getAction() == Status.IDLE && !userStatus.getBeatmapChecksum().isEmpty()) {
      // There is a bug where the client starts playing but
      // doesn't set the status to "Playing".
      userStatus.setAction(Status.PLAYING);
    }

    return userStatus;
  }

  public Message readMessage(InputStream stream) throws IOException {
    Message message = new Message();
    message.setContent(reader.readString(stream));
    message.setTarget("#osu");
    message.setSender("");

    return message;
  }

  public ReplayFrameBundle readSpectateFrames(InputStream stream) throws IOException {
    ReplayFrameBundle bundle = new ReplayFrameBundle();
    List<ReplayFrame> frames = new ArrayList<>();
    int framesSize = reader.readUint16(stream);
    for (int i = 0; i < framesSize; i++) {
      ReplayFrame frame = new ReplayFrame();
      boolean mouseLeft = reader.readBoolean(stream);
      boolean mouseRight = reader.readBoolean(stream);
      frame.setMouseX(reader.readFloat32(stream));
      frame.setMouseY(reader.readFloat32(stream));
      frame.setTime(reader.readInt32(stream));

      int buttonState = 0;
      if (mouseLeft) {
        buttonState |= ButtonState.LEFT_1.getValue();
      }
      if (mouseRight) {
        buttonState |= ButtonState.RIGHT_1.getValue();
      }
      frame.setButtonState(buttonState);

      frames.add(frame);
    }
    ReplayAction action = ReplayAction.fromValue(reader.readUint8(stream));
    bundle.setAction(action);
    bundle.setFrames(frames);

    return bundle;
  }
}
