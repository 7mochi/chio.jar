package pe.nanamochi.clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import pe.nanamochi.objects.Match;
import pe.nanamochi.objects.MatchSlot;
import pe.nanamochi.objects.ScoreFrame;
import pe.nanamochi.objects.SlotStatus;
import pe.nanamochi.packets.Packets;

/**
 * B312 adds the match start & update packets, as well as the "InProgress" boolean inside the match
 * struct.
 */
public class B312 extends B298 {

  public B312(int slotSize, int protocolVersion) {
    super(slotSize, protocolVersion);
  }

  @Override
  public void writeMatchStart(OutputStream stream, Match match) throws IOException {
    writePacket(
        stream, Packets.BANCHO_MATCH_START.getId(), new ByteArrayOutputStream().toByteArray());
  }

  @Override
  public void writeMatchScoreUpdate(OutputStream stream, ScoreFrame frame) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writeScoreFrame(buffer, frame);
    writePacket(stream, Packets.BANCHO_MATCH_SCORE_UPDATE.getId(), buffer.toByteArray());
  }

  @Override
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
    writer.writeBoolean(buffer, match.isInProgress());
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

  @Override
  public Match readMatch(InputStream stream) throws IOException {
    Match match = new Match();
    match.setId(reader.readUint8(stream));
    match.setInProgress(reader.readBoolean(stream));
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
