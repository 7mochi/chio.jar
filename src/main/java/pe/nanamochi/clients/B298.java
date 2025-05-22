package pe.nanamochi.clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import pe.nanamochi.objects.Match;
import pe.nanamochi.objects.MatchSlot;
import pe.nanamochi.objects.SlotStatus;
import pe.nanamochi.packets.Packets;

/** B298 adds a.json partial implementation of multiplayer, as well as fellow spectators. */
public class B298 extends B296 {

  public B298(int slotSize, int protocolVersion) {
    super(slotSize, protocolVersion);
  }

  @Override
  public Object readPacketType(int packetId, InputStream stream) throws IOException {
    if (packetId == Packets.OSU_USER_STATUS.getId()) {
      return readUserStatus(stream);
    } else if (packetId == Packets.OSU_MESSAGE.getId()) {
      return readMessage(stream);
    } else if (packetId == Packets.OSU_PRIVATE_MESSAGE.getId()) {
      return readPrivateMessage(stream);
    } else if (packetId == Packets.OSU_START_SPECTATING.getId()) {
      return reader.readUint32(stream);
    } else if (packetId == Packets.OSU_SPECTATE_FRAMES.getId()) {
      return readSpectateFrames(stream);
    } else if (packetId == Packets.OSU_ERROR_REPORT.getId()) {
      return reader.readString(stream);
    } else if (packetId == Packets.OSU_MATCH_CREATE.getId()) {
      return readMatch(stream);
    } else if (packetId == Packets.OSU_MATCH_JOIN.getId()) {
      return reader.readUint32(stream);
    } else if (packetId == Packets.OSU_MATCH_CHANGE_SETTINGS.getId()) {
      return readMatch(stream);
    } else if (packetId == Packets.OSU_MATCH_CHANGE_SLOT.getId()) {
      return reader.readUint32(stream);
    } else if (packetId == Packets.OSU_MATCH_LOCK.getId()) {
      return reader.readUint32(stream);
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public void writeMatchUpdate(OutputStream stream, Match match) throws IOException {
    if (match.getId() > 255) {
      // Match IDs greater than 255 are not supported in this client
      return;
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writeMatch(buffer, match);

    writePacket(stream, Packets.BANCHO_MATCH_UPDATE.getId(), buffer.toByteArray());
  }

  @Override
  public void writeMatchNew(OutputStream stream, Match match) throws IOException {
    if (match.getId() > 255) {
      // Match IDs greater than 255 are not supported in this client
      return;
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writeMatch(buffer, match);

    writePacket(stream, Packets.BANCHO_MATCH_NEW.getId(), buffer.toByteArray());
  }

  @Override
  public void writeMatchDisband(OutputStream stream, int matchId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, matchId);
    writePacket(stream, Packets.BANCHO_MATCH_DISBAND.getId(), buffer.toByteArray());
  }

  @Override
  public void writeLobbyJoin(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, userId);
    writePacket(stream, Packets.BANCHO_LOBBY_JOIN.getId(), buffer.toByteArray());
  }

  @Override
  public void writeLobbyPart(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, userId);
    writePacket(stream, Packets.BANCHO_LOBBY_PART.getId(), buffer.toByteArray());
  }

  @Override
  public void writeMatchJoinSuccess(OutputStream stream, Match match) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writeMatch(buffer, match);

    writePacket(stream, Packets.BANCHO_MATCH_JOIN_SUCCESS.getId(), buffer.toByteArray());
  }

  @Override
  public void writeMatchJoinFail(OutputStream stream) throws IOException {
    writePacket(
        stream, Packets.BANCHO_MATCH_JOIN_FAIL.getId(), new ByteArrayOutputStream().toByteArray());
  }

  @Override
  public void writeFellowSpectatorJoined(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, userId);

    // Weirdly enough, the client seems to need both of these packets to be sent?
    // If only one is sent, the client will not display the fellow spectator.
    writePacket(stream, Packets.BANCHO_FELLOW_SPECTATOR_JOINED.getId(), buffer.toByteArray());
  }

  @Override
  public void writeFellowSpectatorLeft(OutputStream stream, int userId) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeInt32(buffer, userId);

    writePacket(stream, Packets.BANCHO_FELLOW_SPECTATOR_LEFT.getId(), buffer.toByteArray());
  }

  protected void writeMatch(ByteArrayOutputStream buffer, Match match) throws IOException {
    List<Boolean> slotsOpen = new ArrayList<>();
    List<Boolean> slotsUsed = new ArrayList<>();
    List<Boolean> slotsReady = new ArrayList<>();

    for (MatchSlot slot : match.getSlots()) {
      slotsOpen.add(slot.getStatus() == SlotStatus.OPEN);
      slotsUsed.add(slot.hasPlayer());
      slotsReady.add(slot.getStatus() == SlotStatus.READY);
    }

    writer.writeUint8(buffer, match.getId());
    writer.writeUint8(buffer, match.getType());
    writer.writeString(buffer, match.getName());
    writer.writeString(buffer, match.getBeatmapText());
    writer.writeInt32(buffer, match.getBeatmapId());
    writer.writeString(buffer, match.getBeatmapChecksum());
    writer.writeBoolList(buffer, slotsOpen);
    writer.writeBoolList(buffer, slotsUsed);
    writer.writeBoolList(buffer, slotsReady);

    for (MatchSlot slot : match.getSlots()) {
      if (slot.hasPlayer()) {
        writer.writeInt32(buffer, slot.getUserId());
      }
    }
  }

  public Match readMatch(InputStream stream) throws IOException {
    Match match = new Match();
    match.setId(reader.readUint8(stream));
    match.setType(reader.readUint8(stream));
    match.setName(reader.readString(stream));
    match.setBeatmapText(reader.readString(stream));
    match.setBeatmapId(reader.readInt32(stream));
    match.setBeatmapChecksum(reader.readString(stream));

    List<Boolean> slotsOpen = reader.readBoolList(stream);
    List<Boolean> slotsUsed = reader.readBoolList(stream);
    List<Boolean> slotsReady = reader.readBoolList(stream);

    List<MatchSlot> slots = new ArrayList<>();

    for (int i = 0; i < slotSize; i++) {
      MatchSlot slot = new MatchSlot();
      slot.setStatus(slotsOpen.get(i) ? SlotStatus.OPEN : SlotStatus.LOCKED);
      slot.setStatus(slotsUsed.get(i) ? SlotStatus.NOT_READY : slot.getStatus());
      slot.setStatus(slotsReady.get(i) ? SlotStatus.READY : slot.getStatus());

      if (slot.hasPlayer()) slot.setUserId(reader.readInt32(stream));

      slots.add(slot);
    }

    match.setSlots(slots);

    return match;
  }
}
