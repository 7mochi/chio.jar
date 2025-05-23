package pe.nanamochi.clients;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import pe.nanamochi.objects.ScoreFrame;

/** B296 adds the "Time" value to score frames. */
public class B296 extends B294 {

  public B296(int slotSize, int protocolVersion) {
    super(slotSize, protocolVersion);
  }

  @Override
  protected void writeScoreFrame(ByteArrayOutputStream buffer, ScoreFrame frame)
      throws IOException {
    writer.writeString(buffer, frame.getChecksum());
    writer.writeInt32(buffer, frame.getTime());
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

  @Override
  protected ScoreFrame readScoreFrame(InputStream stream) throws IOException {
    ScoreFrame frame = new ScoreFrame();
    reader.readString(stream); // TODO: Validate checksum
    frame.setTime(reader.readInt32(stream));
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
