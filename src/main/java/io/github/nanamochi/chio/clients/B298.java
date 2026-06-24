package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.MatchSlot;
import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.SlotStatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// b298 adds a partial implementation of multiplayer, as well as fellow spectators.
public class B298 extends B296 {

  // region Packet Reading

  @Override
  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    return switch (type) {
      case OSU_PART_LOBBY, OSU_JOIN_LOBBY, OSU_LEAVE_MATCH, OSU_MATCH_READY -> null;
      case OSU_CREATE_MATCH, OSU_MATCH_CHANGE_SETTINGS -> readMatch(data);
      case OSU_JOIN_MATCH, OSU_MATCH_LOCK, OSU_MATCH_CHANGE_SLOT ->
          reader.readInt32(new ByteArrayInputStream(data));
      default -> super.readPacketType(type, data);
    };
  }

  protected Match readMatch(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    Match match = new Match();
    match.setId(reader.readUInt8(input));
    match.setType(MatchType.fromValue(reader.readUInt8(input)));
    match.setName(reader.readString(input));
    match.setBeatmapText(reader.readString(input));
    match.setBeatmapId(reader.readInt32(input));
    match.setBeatmapChecksum(reader.readString(input));

    List<Boolean> slotsOpen = reader.readBoolList(input, getSlotSize());
    List<Boolean> slotsUsed = reader.readBoolList(input, getSlotSize());
    List<Boolean> slotsReady = reader.readBoolList(input, getSlotSize());

    List<MatchSlot> slots = new ArrayList<>();

    for (int i = 0; i < getSlotSize(); i++) {
      MatchSlot slot = new MatchSlot();
      slot.setStatus(slotsOpen.get(i) ? SlotStatus.OPEN : SlotStatus.LOCKED);
      slot.setStatus(slotsUsed.get(i) ? SlotStatus.NOT_READY : slot.getStatus());
      slot.setStatus(slotsReady.get(i) ? SlotStatus.READY : slot.getStatus());

      if (slot.hasPlayer()) {
        slot.setUserId(reader.readInt32(input));
      }

      slots.add(slot);
    }

    match.setSlots(slots);
    return match;
  }

  // endregion
  // region Packet Writing

  @Override
  public byte[] writePacket(PacketType type, Object... args) throws IOException {
    return switch (type) {
      case BANCHO_MATCH_UPDATE -> writeMatchUpdate((Match) args[0]);
      case BANCHO_MATCH_NEW -> writeMatchNew((Match) args[0]);
      case BANCHO_MATCH_DISBAND -> writeMatchDisband((int) args[0]);
      case BANCHO_LOBBY_JOIN -> writeLobbyJoin((int) args[0]);
      case BANCHO_LOBBY_PART -> writeLobbyPart((int) args[0]);
      case BANCHO_MATCH_JOIN_SUCCESS -> writeMatchJoinSuccess((Match) args[0]);
      case BANCHO_MATCH_JOIN_FAIL -> writeMatchJoinFail();
      case BANCHO_FELLOW_SPECTATOR_JOINED -> writeFellowSpectatorJoined((int) args[0]);
      case BANCHO_FELLOW_SPECTATOR_LEFT -> writeFellowSpectatorLeft((int) args[0]);
      default -> super.writePacket(type, args);
    };
  }

  protected byte[] writeMatchUpdate(Match match) throws IOException {
    if (match.getId() > 0xFF) {
      // Match IDs greater than 255 are not supported in this client
      return new byte[0];
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writeMatch(buf, match);
    return writePacket(PacketType.BANCHO_MATCH_UPDATE, buf.toByteArray());
  }

  protected byte[] writeMatchNew(Match match) throws IOException {
    if (match.getId() > 0xFF) {
      // Match IDs greater than 255 are not supported in this client
      return new byte[0];
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writeMatch(buf, match);
    return writePacket(PacketType.BANCHO_MATCH_NEW, buf.toByteArray());
  }

  protected byte[] writeMatchDisband(int matchId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeInt32(buf, matchId);
    return writePacket(PacketType.BANCHO_MATCH_DISBAND, buf.toByteArray());
  }

  protected byte[] writeLobbyJoin(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeInt32(buf, userId);
    return writePacket(PacketType.BANCHO_LOBBY_JOIN, buf.toByteArray());
  }

  protected byte[] writeLobbyPart(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeInt32(buf, userId);
    return writePacket(PacketType.BANCHO_LOBBY_PART, buf.toByteArray());
  }

  protected byte[] writeMatchJoinSuccess(Match match) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writeMatch(buf, match);
    return writePacket(PacketType.BANCHO_MATCH_JOIN_SUCCESS, buf.toByteArray());
  }

  protected byte[] writeMatchJoinFail() throws IOException {
    return writePacket(PacketType.BANCHO_MATCH_JOIN_FAIL, new byte[0]);
  }

  protected byte[] writeFellowSpectatorJoined(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeInt32(buf, userId);
    return writePacket(PacketType.BANCHO_FELLOW_SPECTATOR_JOINED, buf.toByteArray());
  }

  protected byte[] writeFellowSpectatorLeft(int userId) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeInt32(buf, userId);
    return writePacket(PacketType.BANCHO_FELLOW_SPECTATOR_LEFT, buf.toByteArray());
  }

  protected void writeMatch(ByteArrayOutputStream buf, Match match) throws IOException {
    List<Boolean> slotsOpen = new ArrayList<>();
    List<Boolean> slotsUsed = new ArrayList<>();
    List<Boolean> slotsReady = new ArrayList<>();

    for (MatchSlot slot : match.getSlots()) {
      slotsOpen.add(slot.getStatus() == SlotStatus.OPEN);
      slotsUsed.add(slot.hasPlayer());
      slotsReady.add(slot.getStatus() == SlotStatus.READY);
    }

    writer.writeUInt8(buf, match.getId());
    writer.writeUInt8(buf, match.getType().getValue());
    writer.writeString(buf, match.getName());
    writer.writeString(buf, match.getBeatmapText());
    writer.writeInt32(buf, match.getBeatmapId());
    writer.writeString(buf, match.getBeatmapChecksum());
    writer.writeBoolList(buf, slotsOpen);
    writer.writeBoolList(buf, slotsUsed);
    writer.writeBoolList(buf, slotsReady);

    for (MatchSlot slot : match.getSlots()) {
      if (slot.hasPlayer()) {
        writer.writeInt32(buf, slot.getUserId());
      }
    }
  }

  // endregion
  // region Properties

  @Override
  public int getVersion() {
    return 298;
  }

  // endregion
}
