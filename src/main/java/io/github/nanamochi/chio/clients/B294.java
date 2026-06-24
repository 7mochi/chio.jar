package io.github.nanamochi.chio.clients;

import io.github.nanamochi.chio.model.Message;
import io.github.nanamochi.chio.model.ReplayFrame;
import io.github.nanamochi.chio.model.ReplayFrameBundle;
import io.github.nanamochi.chio.model.ScoreFrame;
import io.github.nanamochi.chio.model.enums.PacketType;
import io.github.nanamochi.chio.model.enums.ReplayAction;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// b294 implements private messages, as well as score frames in spectating.
public class B294 extends B291 {

  // region Packet Reading

  @Override
  protected Object readPacketType(PacketType type, byte[] data) throws IOException {
    if (type == PacketType.OSU_PRIVATE_MESSAGE) {
      return readPrivateMessage(data);
    }
    return super.readPacketType(type, data);
  }

  // endregion
  // region Reader Methods

  // Channel selection has not been implemented yet
  @Override
  protected Message readMessage(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    return new Message("", reader.readString(input), "#osu", 0);
  }

  protected Message readPrivateMessage(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    String target = reader.readString(input);
    String content = reader.readString(input);
    boolean isDirectMessage = reader.readBoolean(input);

    if (!isDirectMessage) {
      throw new IOException("Expected direct message, got channel message");
    }

    return new Message("", content, target, 0);
  }

  @Override
  protected ReplayFrameBundle readSpectateFrames(byte[] data) throws IOException {
    ByteArrayInputStream input = new ByteArrayInputStream(data);
    int frameCount = reader.readUInt16(input);
    List<ReplayFrame> frames = new ArrayList<>(frameCount);

    for (int i = 0; i < frameCount; i++) {
      frames.add(readReplayFrame(input));
    }

    ReplayAction action = ReplayAction.fromValue(reader.readUInt8(input));
    ScoreFrame scoreFrame = null;

    if (input.available() > 0) {
      scoreFrame = readScoreFrame(input);
    }

    return new ReplayFrameBundle(action, frames, scoreFrame, 0);
  }

  protected ScoreFrame readScoreFrame(InputStream input) throws IOException {
    // TODO: Validate checksum
    reader.readString(input);

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
        0,
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
  // region Writer Methods

  // Channel selection has not been implemented yet
  @Override
  protected byte[] writeMessage(Message message) throws IOException {
    if (!message.isDirectMessage() && !getAutojoinChannels().contains(message.getTarget())) {
      return new byte[0];
    }

    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeString(buf, message.getSender());
    writer.writeString(buf, message.getContent());
    writer.writeBoolean(buf, message.isDirectMessage());
    return writePacket(PacketType.BANCHO_MESSAGE, buf.toByteArray());
  }

  @Override
  protected byte[] writeSpectateFrames(ReplayFrameBundle bundle) throws IOException {
    ByteArrayOutputStream buf = new ByteArrayOutputStream();
    writer.writeUInt16(buf, bundle.getFrames().size());

    for (ReplayFrame frame : bundle.getFrames()) {
      buf.write(writeReplayFrame(frame));
    }

    writer.writeUInt8(buf, bundle.getAction().getValue());

    if (bundle.getFrame() != null) {
      writeScoreFrame(buf, bundle.getFrame());
    }

    return writePacket(PacketType.BANCHO_SPECTATE_FRAMES, buf.toByteArray());
  }

  protected void writeScoreFrame(ByteArrayOutputStream buf, ScoreFrame frame) throws IOException {
    writer.writeString(buf, frame.getChecksum());
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
    return 294;
  }

  // endregion
}
