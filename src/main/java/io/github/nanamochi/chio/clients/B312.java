package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.Match;
import io.github.nanamochi.chio.model.MatchSlot;
import io.github.nanamochi.chio.model.ScoreFrame;
import io.github.nanamochi.chio.model.enums.MatchType;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.SlotStatus;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// b312 adds the match start & update packets, as well
// as the "InProgress" boolean inside the match struct.
public class B312 extends B298 {

  // region Packet Reading

  @Override
  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    return switch (type) {
      case OSU_MATCH_START -> null;
      case OSU_MATCH_SCORE_UPDATE -> readScoreFrame(new ByteArrayInputStream(data));
      case OSU_MATCH_COMPLETE -> null;
      default -> super.readPacketType(type, data);
    };
  }

  // endregion
  // region Reader Methods

  @Override
  protected Match readMatch(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    Match match = new Match();
    match.setId(reader.readUInt8(input));
    match.setInProgress(reader.readBoolean(input));
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
      case BANCHO_MATCH_START -> writeMatchStart((Match) args[0]);
      case BANCHO_MATCH_SCORE_UPDATE -> writeMatchScoreUpdate((ScoreFrame) args[0]);
      default -> super.writePacket(type, args);
    };
  }

  // endregion
  // region Writer Methods

  protected byte[] writeMatchStart(Match match) throws IOException {
    return writePacket(PacketType.BANCHO_MATCH_START, new byte[0]);
  }

  protected byte[] writeMatchScoreUpdate(ScoreFrame frame) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writeScoreFrame(buf, frame);
    return writePacket(PacketType.BANCHO_MATCH_SCORE_UPDATE, buf.toByteArray());
  }

  @Override
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
    writer.writeBoolean(buf, match.isInProgress());
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
    return 312;
  }

  // endregion
}
