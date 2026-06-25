package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.MatchSlot;
import io.github.nanamochi.chio.model.ScoreFrame;
import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.SlotStatus;
import io.github.nanamochi.chio.util.GZip;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// b334 introduces a lot of breaking changes:
// - Compression boolean inside packet header
// - Removal of checksums in score frames
// - Mods inside match struct
// - Packet IDs 50-58
public class B334 extends B323 {

  @Override
  protected PacketType convertInputPacket(int packet) {
    if (packet == 11) return PacketType.BANCHO_IRC_JOIN;
    if (packet == 51) return PacketType.OSU_MATCH_CHANGE_BEATMAP;
    if (packet > 11) packet--;
    if (packet >= 51) packet--;

    return PacketType.fromValue(packet);
  }

  @Override
  protected int convertOutputPacket(PacketType packet) {
    if (packet == PacketType.BANCHO_IRC_JOIN) return 11;
    if (packet == PacketType.OSU_MATCH_CHANGE_BEATMAP) return 51;

    int value = packet.getValue();

    if (value >= 11) value++;
    if (value >= 51) value++;

    return value;
  }

  // region Packet Reading

  @Override
  public Object readPacket(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);

    int rawPacketId = reader.readUInt16(input);
    PacketType packetType = convertInputPacket(rawPacketId);

    boolean compression = reader.readBoolean(input);

    int length = reader.readInt32(input);
    byte[] packetData = new byte[length];
    input.read(packetData);

    if (compression) {
      packetData = GZip.decompress(packetData);
    }

    return readPacketType(packetType, packetData);
  }

  @Override
  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    return switch (type) {
      case OSU_MATCH_CHANGE_MODS -> readMatchChangeMods(data);
      case OSU_MATCH_LOAD_COMPLETE, OSU_MATCH_NO_BEATMAP, OSU_MATCH_NOT_READY, OSU_MATCH_FAILED ->
          null;
      default -> super.readPacketType(type, data);
    };
  }

  // endregion
  // region Reader Methods

  protected int readMatchChangeMods(byte[] data) throws IOException {
    return reader.readInt32(new ByteArrayInputStream(data));
  }

  @Override
  protected Match readMatch(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    Match match = new Match();
    match.setId(reader.readUInt8(input));
    match.setInProgress(reader.readBoolean(input));
    match.setType(MatchType.fromValue(reader.readUInt8(input)));
    match.setMods(reader.readUInt16(input));
    match.setName(reader.readString(input));
    match.setBeatmapText(reader.readString(input));
    match.setBeatmapId(reader.readInt32(input));
    match.setBeatmapChecksum(reader.readString(input));

    List<MatchSlot> slots = new ArrayList<>();

    for (int i = 0; i < getSlotSize(); i++) {
      MatchSlot slot = new MatchSlot();
      slot.setStatus(SlotStatus.fromValue(reader.readUInt8(input)));
      slots.add(slot);
    }

    for (MatchSlot slot : slots) {
      if (slot.hasPlayer()) {
        slot.setUserId(reader.readInt32(input));
      }
    }

    match.setSlots(slots);
    return match;
  }

  @Override
  protected ScoreFrame readScoreFrame(java.io.InputStream input) throws IOException {
    int time = reader.readInt32(input);
    int id = reader.readUInt8(input);
    int total300 = reader.readUInt16(input);
    int total100 = reader.readUInt16(input);
    int total50 = reader.readUInt16(input);
    int totalGeki = reader.readUInt16(input);
    int totalKatu = reader.readUInt16(input);
    int totalMiss = reader.readUInt16(input);
    long totalScore = reader.readUInt32(input);
    int maxCombo = reader.readUInt16(input);
    int currentCombo = reader.readUInt16(input);
    boolean perfect = reader.readBoolean(input);
    int hp = reader.readUInt8(input);

    return new ScoreFrame(
        time,
        id,
        total300,
        total100,
        total50,
        totalGeki,
        totalKatu,
        totalMiss,
        (int) totalScore,
        maxCombo,
        currentCombo,
        perfect,
        hp,
        0);
  }

  // endregion
  // region Packet Writing

  @Override
  public byte[] writePacket(PacketType type, Object... args) throws IOException {
    return switch (type) {
      case BANCHO_MATCH_TRANSFER_HOST -> writeMatchTransferHost();
      case BANCHO_MATCH_ALL_PLAYERS_LOADED -> writeMatchAllPlayersLoaded();
      case BANCHO_MATCH_PLAYER_FAILED -> writeMatchPlayerFailed((int) args[0]);
      case BANCHO_MATCH_COMPLETE -> writeMatchComplete();
      default -> super.writePacket(type, args);
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
    boolean compressionEnabled = data.length > 150 && !isDisableCompression();
    byte[] compressed = compressionEnabled ? GZip.compress(data, includeMTime) : data;

    writer.writeUInt16(output, packet);
    writer.writeBoolean(output, compressionEnabled);
    writer.writeUInt32(output, compressed.length);
    output.write(compressed);

    return output.toByteArray();
  }

  // endregion
  // region Writer Methods

  protected byte[] writeMatchTransferHost() throws IOException {
    return writePacket(PacketType.BANCHO_MATCH_TRANSFER_HOST, new byte[0]);
  }

  protected byte[] writeMatchAllPlayersLoaded() throws IOException {
    return writePacket(PacketType.BANCHO_MATCH_ALL_PLAYERS_LOADED, new byte[0]);
  }

  protected byte[] writeMatchPlayerFailed(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeInt32(buf, userId);
    return writePacket(PacketType.BANCHO_MATCH_PLAYER_FAILED, buf.toByteArray());
  }

  protected byte[] writeMatchComplete() throws IOException {
    return writePacket(PacketType.BANCHO_MATCH_COMPLETE, new byte[0]);
  }

  @Override
  protected byte[] writeMatchStart(Match match) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writeMatch(buf, match);
    return writePacket(PacketType.BANCHO_MATCH_START, buf.toByteArray());
  }

  @Override
  protected byte[] writeMatchScoreUpdate(ScoreFrame frame) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writeScoreFrame(buf, frame);
    return writePacket(PacketType.BANCHO_MATCH_SCORE_UPDATE, buf.toByteArray());
  }

  @Override
  protected void writeMatch(ByteArrayOutputStream buf, Match match) throws IOException {
    writer.writeUInt8(buf, match.getId());
    writer.writeBoolean(buf, match.isInProgress());
    writer.writeUInt8(buf, match.getType().getValue());
    writer.writeUInt16(buf, match.getMods());
    writer.writeString(buf, match.getName());
    writer.writeString(buf, match.getBeatmapText());
    writer.writeInt32(buf, match.getBeatmapId());
    writer.writeString(buf, match.getBeatmapChecksum());

    for (MatchSlot slot : match.getSlots()) {
      writer.writeUInt8(buf, slot.getStatus().getValue());
    }

    for (MatchSlot slot : match.getSlots()) {
      if (slot.hasPlayer()) {
        writer.writeInt32(buf, slot.getUserId());
      }
    }
  }

  @Override
  protected void writeScoreFrame(ByteArrayOutputStream buf, ScoreFrame frame) throws IOException {
    writer.writeInt32(buf, frame.getTime());
    writer.writeUInt8(buf, frame.getId());
    writer.writeUInt16(buf, frame.getTotal300());
    writer.writeUInt16(buf, frame.getTotal100());
    writer.writeUInt16(buf, frame.getTotal50());
    writer.writeUInt16(buf, frame.getTotalGeki());
    writer.writeUInt16(buf, frame.getTotalKatu());
    writer.writeUInt16(buf, frame.getTotalMiss());
    writer.writeUInt32(buf, frame.getTotalScore());
    writer.writeUInt16(buf, frame.getMaxCombo());
    writer.writeUInt16(buf, frame.getCurrentCombo());
    writer.writeBoolean(buf, frame.isPerfect());
    writer.writeUInt8(buf, frame.getHp());
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 334;
  }

  @Override
  public int getHeaderSize() {
    return 7;
  }

  // endregion
}
