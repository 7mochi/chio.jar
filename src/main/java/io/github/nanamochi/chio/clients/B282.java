package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.BanchoIO;
import io.github.nanamochi.chio.io.DataReader;
import io.github.nanamochi.chio.io.DataWriter;
import io.github.nanamochi.chio.io.impl.BanchoDataReader;
import io.github.nanamochi.chio.io.impl.BanchoDataWriter;
import io.github.nanamochi.chio.model.Message;
import io.github.nanamochi.chio.model.ReplayFrame;
import io.github.nanamochi.chio.model.ReplayFrameBundle;
import io.github.nanamochi.chio.model.UserInfo;
import io.github.nanamochi.chio.model.UserQuit;
import io.github.nanamochi.chio.model.UserStatus;
import io.github.nanamochi.chio.model.enums.ButtonState;
import io.github.nanamochi.chio.model.enums.Mods;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.QuitState;
import io.github.nanamochi.chio.model.enums.ReplayAction;
import io.github.nanamochi.chio.model.enums.Status;
import io.github.nanamochi.chio.util.GZip;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// b282 is the initial implementation of the bancho protocol.
// Every following version will be based on it.
public class B282 implements BanchoIO {
  protected final DataReader reader = new BanchoDataReader();
  protected final DataWriter writer = new BanchoDataWriter();

  // region Packet Reading

  @Override
  public Object readPacket(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);

    int rawPacketId = reader.readUInt16(input);
    PacketType packetType = convertInputPacket(rawPacketId);

    int length = reader.readInt32(input);
    byte[] compressed = new byte[length];
    input.read(compressed);
    byte[] decompressed = GZip.decompress(compressed);

    return readPacketType(packetType, decompressed);
  }

  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    return switch (type) {
      case OSU_USER_STATUS -> readUserStatus(data);
      case OSU_MESSAGE -> readMessage(data);
      case OSU_EXIT -> readExit(data);
      case OSU_START_SPECTATING -> readStartSpectating(data);
      case OSU_SPECTATE_FRAMES -> readSpectateFrames(data);
      case OSU_ERROR_REPORT -> readErrorReport(data);
      case OSU_PONG, OSU_REQUEST_STATUS, OSU_STOP_SPECTATING, OSU_CANT_SPECTATE -> null;
      default -> throw new UnsupportedOperationException("Unsupported packet type: " + type);
    };
  }

  // endregion
  // region Reader Methods

  protected UserStatus readUserStatus(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);

    UserStatus status = new UserStatus();
    status.setAction(convertInputStatus(reader.readUInt8(input)));

    if (status.getAction() != Status.UNKNOWN) {
      status.setText(reader.readString(input));
      status.setBeatmapChecksum(reader.readString(input));
      status.setMods(Mods.fromBitmask(reader.readUInt16(input)));
    }

    // Bug fix: client starts playing but doesn't set status
    if (status.getAction() == Status.IDLE && !status.getBeatmapChecksum().isEmpty()) {
      status.setAction(Status.PLAYING);
    }

    return status;
  }

  // Private messages & channels have not been implemented yet
  protected Message readMessage(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    return new Message("", reader.readString(input), "#osu", 0);
  }

  protected boolean readExit(byte[] data) {
    return false;
  }

  protected int readStartSpectating(byte[] data) throws IOException {
    return reader.readInt32(new ByteArrayInputStream(data));
  }

  protected ReplayFrameBundle readSpectateFrames(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    int frameCount = reader.readUInt16(input);
    List<ReplayFrame> frames = new ArrayList<>(frameCount);
    for (int i = 0; i < frameCount; i++) {
      frames.add(readReplayFrame(input));
    }
    ReplayAction action = ReplayAction.fromValue(reader.readUInt8(input));
    return new ReplayFrameBundle(action, frames, null, 0);
  }

  protected ReplayFrame readReplayFrame(InputStream input) throws IOException {
    boolean mouseLeft = reader.readBoolean(input);
    boolean mouseRight = reader.readBoolean(input);
    float mouseX = reader.readFloat32(input);
    float mouseY = reader.readFloat32(input);
    int time = reader.readInt32(input);
    int buttonState = 0;

    if (mouseLeft) buttonState |= ButtonState.LEFT_1.getValue();
    if (mouseRight) buttonState |= ButtonState.RIGHT_1.getValue();

    return new ReplayFrame(buttonState, mouseX, mouseY, time);
  }

  protected String readErrorReport(byte[] data) throws IOException {
    return reader.readString(new ByteArrayInputStream(data));
  }

  // endregion
  // region Packet Conversion

  // Convert a packet from the client to a modern packet type.
  protected PacketType convertInputPacket(int packet) {
    if (packet == 11) return PacketType.BANCHO_IRC_JOIN;
    if (packet > 11 && packet <= 45) packet--;
    if (packet > 50) packet--;

    return PacketType.fromValue(packet);
  }

  // Convert a modern packet type to a packet the client understands.
  protected int convertOutputPacket(PacketType packet) {
    int value = packet.getValue();
    if (packet == PacketType.BANCHO_IRC_JOIN) return 11;
    if (value >= 11 && value < 45) return value + 1;
    if (value > 50) return value + 1;

    return value;
  }

  // Convert input status from legacy format.
  protected Status convertInputStatus(int status) {
    if (status == 10) return Status.UNKNOWN;
    if (status > 9) return Status.fromValue(status - 1);

    return Status.fromValue(status);
  }

  // Convert output status to legacy format.
  protected Status convertOutputStatus(UserStatus status) {
    if (status.isUpdateStats()) {
      // This will make the client update the user's stats.
      // It will not be present in later versions.
      return Status.STATS_UPDATE;
    }

    if (status.getAction().getValue() > Status.SUBMITTING.getValue())
      return Status.fromValue(status.getAction().getValue() - 1);

    return status.getAction();
  }

  // endregion
  // region Packet Writing

  @Override
  public byte[] writePacket(PacketType type, Object... args) throws IOException {
    return switch (type) {
      case BANCHO_LOGIN_REPLY -> writeLoginReply((int) args[0]);
      case BANCHO_PING -> writePing();
      case BANCHO_MESSAGE -> writeMessage((Message) args[0]);
      case BANCHO_IRC_CHANGE_USERNAME -> writeIrcChangeUsername((String) args[0], (String) args[1]);
      case BANCHO_USER_STATS -> writeUserStats((UserInfo) args[0]);
      case BANCHO_IRC_JOIN -> writeIrcJoin((String) args[0]);
      case BANCHO_IRC_QUIT -> writeIrcQuit((String) args[0]);
      case BANCHO_SPECTATOR_JOINED -> writeSpectatorJoined((int) args[0]);
      case BANCHO_SPECTATOR_LEFT -> writeSpectatorLeft((int) args[0]);
      case BANCHO_SPECTATE_FRAMES -> writeSpectateFrames((ReplayFrameBundle) args[0]);
      case BANCHO_VERSION_UPDATE -> writeVersionUpdate();
      case BANCHO_SPECTATOR_CANT_SPECTATE -> writeSpectatorCantSpectate((int) args[0]);
      case BANCHO_USER_PRESENCE -> writeUserPresence((UserInfo) args[0]);
      case BANCHO_USER_PRESENCE_SINGLE -> writeUserPresenceSingle((UserInfo) args[0]);
      case BANCHO_USER_PRESENCE_BUNDLE -> {
        List<UserInfo> users = new ArrayList<>();
        for (Object arg : args) users.add((UserInfo) arg);
        yield writeUserPresenceBundle(users);
      }
      case BANCHO_INVITE -> writeInvite((Message) args[0]);
      case BANCHO_USER_QUIT -> writeUserQuit((UserQuit) args[0]);
      default -> throw new UnsupportedOperationException("Unsupported packet type: " + type);
    };
  }

  @Override
  public byte[] writePacket(PacketType type, byte[] data) throws IOException {
    return writePacket(type, data, false);
  }

  @Override
  public byte[] writePacket(PacketType type, byte[] data, boolean includeMTime) throws IOException {
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    int packet = convertOutputPacket(type);
    byte[] compressed = GZip.compress(data, includeMTime);

    writer.writeUInt16(output, packet);
    writer.writeUInt32(output, compressed.length);
    output.write(compressed);

    return output.toByteArray();
  }

  // endregion
  // region Writer Methods

  protected byte[] writeLoginReply(int reply) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, reply);
    return writePacket(PacketType.BANCHO_LOGIN_REPLY, buffer.toByteArray());
  }

  protected byte[] writePing() throws IOException {
    return writePacket(PacketType.BANCHO_PING, new byte[0]);
  }

  // Private messages & channels have not been implemented yet
  protected byte[] writeMessage(Message message) throws IOException {
    if (!getAutojoinChannels().contains(message.getTarget())) {
      return new byte[0];
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeString(buffer, message.getSender());
    writer.writeString(buffer, message.getContent());
    return writePacket(PacketType.BANCHO_MESSAGE, buffer.toByteArray());
  }

  protected byte[] writeIrcChangeUsername(String oldName, String newName) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeString(buffer, oldName + ">>>>" + newName);
    return writePacket(PacketType.BANCHO_IRC_CHANGE_USERNAME, buffer.toByteArray());
  }

  protected byte[] writeIrcJoin(String name) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeString(buf, name);
    return writePacket(PacketType.BANCHO_IRC_JOIN, buf.toByteArray());
  }

  protected byte[] writeIrcQuit(String name) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeString(buf, name);
    return writePacket(PacketType.BANCHO_IRC_QUIT, buf.toByteArray());
  }

  protected byte[] writeSpectatorJoined(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt32(buf, userId);
    return writePacket(PacketType.BANCHO_SPECTATOR_JOINED, buf.toByteArray());
  }

  protected byte[] writeSpectatorLeft(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt32(buf, userId);
    return writePacket(PacketType.BANCHO_SPECTATOR_LEFT, buf.toByteArray());
  }

  protected byte[] writeSpectateFrames(ReplayFrameBundle bundle) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt16(buf, bundle.getFrames().size());

    for (ReplayFrame frame : bundle.getFrames()) {
      buf.write(writeReplayFrame(frame));
    }

    writer.writeUInt8(buf, bundle.getAction().getValue());
    return writePacket(PacketType.BANCHO_SPECTATE_FRAMES, buf.toByteArray());
  }

  protected byte[] writeReplayFrame(ReplayFrame frame) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    boolean leftMouse =
        ButtonState.LEFT_1.isSet(frame.getButtonState())
            || ButtonState.LEFT_2.isSet(frame.getButtonState());
    boolean rightMouse =
        ButtonState.RIGHT_1.isSet(frame.getButtonState())
            || ButtonState.RIGHT_2.isSet(frame.getButtonState());
    writer.writeBoolean(buf, leftMouse);
    writer.writeBoolean(buf, rightMouse);
    writer.writeFloat32(buf, frame.getMouseX());
    writer.writeFloat32(buf, frame.getMouseY());
    writer.writeInt32(buf, frame.getTime());
    return buf.toByteArray();
  }

  protected byte[] writeVersionUpdate() throws IOException {
    return writePacket(PacketType.BANCHO_VERSION_UPDATE, new byte[0]);
  }

  protected byte[] writeSpectatorCantSpectate(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt32(buf, userId);
    return writePacket(PacketType.BANCHO_SPECTATOR_CANT_SPECTATE, buf.toByteArray());
  }

  // b282 does not support user presences, send stats instead
  protected byte[] writeUserPresence(UserInfo info) throws IOException {
    return writeUserStats(info);
  }

  protected byte[] writeUserPresenceSingle(UserInfo info) throws IOException {
    return writeUserPresence(info);
  }

  protected byte[] writeUserPresenceBundle(List<UserInfo> infos) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    for (UserInfo info : infos) {
      buf.write(writeUserPresenceSingle(info));
    }
    return buf.toByteArray();
  }

  // b282 does not support invites, send as regular message
  protected byte[] writeInvite(Message message) throws IOException {
    return writeMessage(message);
  }

  protected byte[] writeUserStatsBody(UserInfo info) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt32(buf, info.getId());
    writer.writeString(buf, info.getName());
    writer.writeUInt64(buf, info.getStats().getRankedScore());
    writer.writeFloat64(buf, info.getStats().getAccuracy());
    writer.writeUInt32(buf, info.getStats().getPlaycount());
    writer.writeUInt64(buf, info.getStats().getTotalScore());
    writer.writeUInt32(buf, info.getStats().getRank());
    writer.writeString(buf, info.getAvatarFilename());
    buf.write(writeStatusUpdate(info.getStatus()));
    writer.writeUInt8(buf, info.getPresence().getTimezone() + 24);
    writer.writeString(buf, info.getPresence().getLocation());
    return buf.toByteArray();
  }

  protected byte[] writeUserStats(UserInfo info) throws IOException {
    if (info.getPresence().isIrc()) {
      return writeIrcJoin(info.getName());
    }
    return writePacket(PacketType.BANCHO_USER_STATS, writeUserStatsBody(info));
  }

  protected byte[] writeStatusUpdate(UserStatus status) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    Status action = convertOutputStatus(status);
    writer.writeUInt8(buf, action.getValue());
    if (action != Status.UNKNOWN) {
      writer.writeString(buf, status.getText());
      writer.writeString(buf, status.getBeatmapChecksum());
      writer.writeUInt16(buf, Mods.toBitmask(status.getMods()));
    }
    return buf.toByteArray();
  }

  // Send user stats with UserQuit packet type
  protected byte[] writeUserQuit(UserQuit quit) throws IOException {
    if (quit.getState() == QuitState.OSU_REMAINING) return new byte[0];

    if (quit.getInfo().getPresence().isIrc()) {
      if (quit.getState() != QuitState.IRC_REMAINING) {
        return writeIrcQuit(quit.getInfo().getName());
      }
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      writer.writeString(buf, quit.getInfo().getName());
      return writePacket(PacketType.BANCHO_USER_QUIT, buf.toByteArray());
    }

    return writePacket(PacketType.BANCHO_USER_QUIT, writeUserStatsBody(quit.getInfo()));
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 282;
  }

  @Override
  public int getSlotSize() {
    return 8;
  }

  @Override
  public int getHeaderSize() {
    return 6;
  }

  @Override
  public int getProtocolVersion() {
    return 0;
  }

  @Override
  public boolean isFormatChatLinks() {
    return true;
  }

  @Override
  public boolean isDisableCompression() {
    return false;
  }

  @Override
  public boolean isRequiresStatusUpdates() {
    return true;
  }

  @Override
  public List<String> getAutojoinChannels() {
    return Arrays.asList("#osu", "#announce");
  }

  @Override
  public String formatChatLink(String text, String url) {
    return "(" + text + ")[" + url + "]";
  }

  // endregion
}
