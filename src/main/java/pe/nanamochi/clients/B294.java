package pe.nanamochi.clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import pe.nanamochi.objects.Message;
import pe.nanamochi.objects.ReplayFrame;
import pe.nanamochi.objects.ReplayFrameBundle;
import pe.nanamochi.objects.ScoreFrame;
import pe.nanamochi.objects.enums.ButtonState;
import pe.nanamochi.objects.enums.ReplayAction;
import pe.nanamochi.packets.Packets;

/** B294 implements private messages, as well as score frames in spectating. */
public class B294 extends B291 {

  public B294(int slotSize, int protocolVersion) {
    super(slotSize, protocolVersion);
  }

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
    } else {
      throw new UnsupportedOperationException("Not implemented yet");
    }
  }

  @Override
  public void writeMessage(OutputStream stream, Message message) throws IOException {
    if (!message.isDirectMessage() && !"#osu".equals(message.getTarget())) {
      return;
    }

    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    writer.writeString(buffer, message.getSender());
    writer.writeString(buffer, message.getContent());
    writer.writeBoolean(buffer, message.isDirectMessage());

    writePacket(stream, Packets.BANCHO_MESSAGE.getId(), buffer.toByteArray());
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

    if (bundle.getFrame() != null) {
      writeScoreFrame(buffer, bundle.getFrame());
    }

    writePacket(stream, Packets.BANCHO_SPECTATE_FRAMES.getId(), buffer.toByteArray());
  }

  protected void writeScoreFrame(ByteArrayOutputStream buffer, ScoreFrame frame)
      throws IOException {
    writer.writeString(buffer, frame.getChecksum());
    writer.writeUint8(buffer, frame.getId());
    writer.writeUint16(buffer, frame.getTotal300());
    writer.writeUint16(buffer, frame.getTotal100());
    writer.writeUint16(buffer, frame.getTotal50());
    writer.writeUint16(buffer, frame.getTotalGeki());
    writer.writeUint16(buffer, frame.getTotalKatu());
    writer.writeUint16(buffer, frame.getTotalMiss());
    writer.writeUint32(buffer, frame.getTotalScore());
    writer.writeUint16(buffer, frame.getMaxCombo());
    writer.writeUint16(buffer, frame.getCurrentCombo());
    writer.writeBoolean(buffer, frame.isPerfect());
    writer.writeUint8(buffer, frame.getHp());
  }

  public Message readPrivateMessage(InputStream stream) throws IOException {
    String target = reader.readString(stream);
    String content = reader.readString(stream);
    boolean isDirectMessage = reader.readBoolean(stream);

    if (!isDirectMessage) {
      throw new IOException("Expected direct message, got channel message");
    }

    return new Message("", content, target, 0);
  }

  @Override
  public ReplayFrameBundle readSpectateFrames(InputStream stream) throws IOException {
    ReplayFrameBundle bundle = new ReplayFrameBundle();
    List<ReplayFrame> frames = new ArrayList<>();
    for (int i = 0; i < reader.readUint16(stream); i++) {
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

    ScoreFrame frame = null;

    if (stream.available() > 0) {
      frame = readScoreFrame(stream);
    }

    ReplayAction action = ReplayAction.fromValue(reader.readUint8(stream));
    bundle.setAction(action);
    bundle.setFrames(frames);
    bundle.setFrame(frame);

    return bundle;
  }

  protected ScoreFrame readScoreFrame(InputStream stream) throws IOException {
    ScoreFrame frame = new ScoreFrame();
    frame.setTime(0);
    // reader.readString(stream); // TODO: Validate checksum
    frame.setId(reader.readUint8(stream));
    frame.setTotal300(reader.readUint16(stream));
    frame.setTotal100(reader.readUint16(stream));
    frame.setTotal50(reader.readUint16(stream));
    frame.setTotalGeki(reader.readUint16(stream));
    frame.setTotalKatu(reader.readUint16(stream));
    frame.setTotalMiss(reader.readUint16(stream));
    frame.setTotalScore(reader.readUint32(stream));
    frame.setMaxCombo(reader.readUint16(stream));
    frame.setCurrentCombo(reader.readUint16(stream));
    frame.setPerfect(reader.readBoolean(stream));
    frame.setHp(reader.readUint8(stream));
    return frame;
  }
}
